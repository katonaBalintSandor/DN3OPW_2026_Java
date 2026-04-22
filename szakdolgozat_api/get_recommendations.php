<?php
header("Content-Type: application/json; charset=UTF-8");
require_once "db.php";

function normalize_scores(array $scores): array {
    if (empty($scores)) {
        return [];
    }

    $values = array_values($scores);
    $min = min($values);
    $max = max($values);

    if ($max == $min) {
        $out = [];
        foreach ($scores as $k => $v) {
            $out[$k] = 1.0;
        }
        return $out;
    }

    $out = [];
    foreach ($scores as $k => $v) {
        $out[$k] = ($v - $min) / ($max - $min);
    }
    return $out;
}

function dot_product(array $a, array $b): float {
    $sum = 0.0;
    foreach ($a as $key => $val) {
        if (isset($b[$key])) {
            $sum += $val * $b[$key];
        }
    }
    return $sum;
}

function vector_norm(array $v): float {
    $sum = 0.0;
    foreach ($v as $val) {
        $sum += $val * $val;
    }
    return sqrt($sum);
}

function cosine_similarity_assoc(array $a, array $b): float {
    $normA = vector_norm($a);
    $normB = vector_norm($b);

    if ($normA <= 0.0 || $normB <= 0.0) {
        return 0.0;
    }

    return dot_product($a, $b) / ($normA * $normB);
}

function tokenize_text(string $text): array {
    $text = mb_strtolower($text, 'UTF-8');
    $text = preg_replace('/[^\p{L}\p{N}\s]+/u', ' ', $text);
    $text = preg_replace('/\s+/u', ' ', trim($text));

    if ($text === '') {
        return [];
    }

    $parts = explode(' ', $text);
    $tokens = [];

    foreach ($parts as $p) {
        $p = trim($p);
        if ($p === '' || mb_strlen($p, 'UTF-8') < 2) {
            continue;
        }
        $tokens[] = $p;
    }

    return $tokens;
}

function build_book_text(array $book): string {
    return trim(
        ($book["title"] ?? "") . " " .
        ($book["author"] ?? "") . " " .
        ($book["category"] ?? "") . " " .
        ($book["description"] ?? "")
    );
}

function term_frequency(array $tokens): array {
    $tf = [];
    foreach ($tokens as $t) {
        $tf[$t] = ($tf[$t] ?? 0) + 1;
    }

    $count = count($tokens);
    if ($count > 0) {
        foreach ($tf as $term => $val) {
            $tf[$term] = $val / $count;
        }
    }

    return $tf;
}

function build_content_vectors(array $books): array {
    $docTokens = [];
    $df = [];
    $docCount = count($books);

    foreach ($books as $book) {
        $bookId = (int)$book["id"];
        $tokens = tokenize_text(build_book_text($book));
        $docTokens[$bookId] = $tokens;

        $unique = array_unique($tokens);
        foreach ($unique as $term) {
            $df[$term] = ($df[$term] ?? 0) + 1;
        }
    }

    $idf = [];
    foreach ($df as $term => $freq) {
        $idf[$term] = log(($docCount + 1) / ($freq + 1)) + 1.0;
    }

    $vectors = [];
    foreach ($docTokens as $bookId => $tokens) {
        $tf = term_frequency($tokens);
        $vec = [];
        foreach ($tf as $term => $tfVal) {
            $vec[$term] = $tfVal * ($idf[$term] ?? 1.0);
        }
        $vectors[$bookId] = $vec;
    }

    return $vectors;
}

$user_id = intval($_GET["user_id"] ?? $_POST["user_id"] ?? 0);
$limit = intval($_GET["limit"] ?? $_POST["limit"] ?? 6);

if ($user_id <= 0) {
    echo json_encode([
        "success" => false,
        "message" => "Hiányzó vagy hibás user_id",
        "books" => []
    ], JSON_UNESCAPED_UNICODE);
    exit;
}

if ($limit <= 0) {
    $limit = 6;
}

$limit = min($limit, 6);

$checkUser = $conn->prepare("SELECT id FROM users WHERE id = ? LIMIT 1");
if (!$checkUser) {
    echo json_encode([
        "success" => false,
        "message" => "Prepare hiba (user check)!",
        "error" => $conn->error,
        "books" => []
    ], JSON_UNESCAPED_UNICODE);
    exit;
}

$checkUser->bind_param("i", $user_id);
$checkUser->execute();
$userResult = $checkUser->get_result();

if ($userResult->num_rows === 0) {
    $checkUser->close();
    echo json_encode([
        "success" => false,
        "message" => "A felhasználó nem létezik.",
        "books" => []
    ], JSON_UNESCAPED_UNICODE);
    exit;
}
$checkUser->close();

$sqlCandidates = "
    SELECT
        b.id,
        b.title,
        b.author,
        b.picture,
        b.category,
        b.description,
        b.release_date,
        ls.library_id,
        ls.quantity,
        COALESCE(tp.topic_count, 0) AS popularity
    FROM library_stock ls
    INNER JOIN books b ON b.id = ls.book_id
    LEFT JOIN (
        SELECT book_id, COUNT(*) AS topic_count
        FROM topics
        GROUP BY book_id
    ) tp ON tp.book_id = b.id
    WHERE ls.quantity > 0
    ORDER BY b.id ASC, ls.quantity DESC
";

$resCandidates = $conn->query($sqlCandidates);
if (!$resCandidates) {
    echo json_encode([
        "success" => false,
        "message" => "Lekérdezési hiba (candidate books)!",
        "error" => $conn->error,
        "books" => []
    ], JSON_UNESCAPED_UNICODE);
    exit;
}

$bestByBook = [];

while ($row = $resCandidates->fetch_assoc()) {
    $bookId = (int)$row["id"];
    $quantity = (int)($row["quantity"] ?? 0);

    if (!isset($bestByBook[$bookId])) {
        $bestByBook[$bookId] = [
            "id" => $bookId,
            "title" => $row["title"] ?? "",
            "author" => $row["author"] ?? "",
            "picture" => $row["picture"] ?? "",
            "category" => $row["category"] ?? "",
            "description" => $row["description"] ?? "",
            "release_date" => $row["release_date"] ?? null,
            "library_id" => (int)($row["library_id"] ?? 0),
            "quantity" => $quantity,
            "popularity" => (int)($row["popularity"] ?? 0),
        ];
    } elseif ($quantity > (int)$bestByBook[$bookId]["quantity"]) {
        $bestByBook[$bookId]["library_id"] = (int)($row["library_id"] ?? 0);
        $bestByBook[$bookId]["quantity"] = $quantity;
    }
}

$candidateBooks = array_values($bestByBook);

if (empty($candidateBooks)) {
    echo json_encode([
        "success" => true,
        "books" => []
    ], JSON_UNESCAPED_UNICODE);
    $conn->close();
    exit;
}

$leasedBookIds = [];
$ratedBookIds = [];

$stmtLeaseHist = $conn->prepare("
    SELECT DISTINCT book_id
    FROM on_lease
    WHERE user_id = ?
");
$stmtLeaseHist->bind_param("i", $user_id);
$stmtLeaseHist->execute();
$resLeaseHist = $stmtLeaseHist->get_result();
while ($row = $resLeaseHist->fetch_assoc()) {
    $leasedBookIds[(int)$row["book_id"]] = true;
}
$stmtLeaseHist->close();

$stmtRatedHist = $conn->prepare("
    SELECT DISTINCT book_id
    FROM topics
    WHERE user_id = ?
");
$stmtRatedHist->bind_param("i", $user_id);
$stmtRatedHist->execute();
$resRatedHist = $stmtRatedHist->get_result();
while ($row = $resRatedHist->fetch_assoc()) {
    $ratedBookIds[(int)$row["book_id"]] = true;
}
$stmtRatedHist->close();

$interactedBookIds = $leasedBookIds + $ratedBookIds;

$contentScores = [];

if (!empty($interactedBookIds)) {
    $contentVectors = build_content_vectors($candidateBooks);

    $interactedVectors = [];
    foreach ($interactedBookIds as $bookId => $_) {
        if (isset($contentVectors[$bookId])) {
            $interactedVectors[] = $contentVectors[$bookId];
        }
    }

    if (!empty($interactedVectors)) {
        $userProfile = [];
        $countProfiles = count($interactedVectors);

        foreach ($interactedVectors as $vec) {
            foreach ($vec as $term => $value) {
                $userProfile[$term] = ($userProfile[$term] ?? 0.0) + $value;
            }
        }

        foreach ($userProfile as $term => $value) {
            $userProfile[$term] = $value / $countProfiles;
        }

        foreach ($candidateBooks as $book) {
            $bookId = (int)$book["id"];
            if (isset($interactedBookIds[$bookId])) {
                continue;
            }

            $bookVec = $contentVectors[$bookId] ?? [];
            $contentScores[$bookId] = cosine_similarity_assoc($userProfile, $bookVec);
        }
    }
}

$interactionRows = [];

$sqlLeaseInteractions = "
    SELECT
        user_id,
        book_id,
        COUNT(*) * 1.0 AS strength
    FROM on_lease
    GROUP BY user_id, book_id
";
$resLeaseInteractions = $conn->query($sqlLeaseInteractions);
if ($resLeaseInteractions) {
    while ($row = $resLeaseInteractions->fetch_assoc()) {
        $u = (int)$row["user_id"];
        $b = (int)$row["book_id"];
        $s = (float)$row["strength"];
        $key = $u . "_" . $b;
        $interactionRows[$key] = [
            "user_id" => $u,
            "book_id" => $b,
            "strength" => $s
        ];
    }
}

$sqlTopicInteractions = "
    SELECT
        user_id,
        book_id,
        AVG(COALESCE(rating, 0)) * 1.5 AS strength
    FROM topics
    GROUP BY user_id, book_id
";
$resTopicInteractions = $conn->query($sqlTopicInteractions);
if ($resTopicInteractions) {
    while ($row = $resTopicInteractions->fetch_assoc()) {
        $u = (int)$row["user_id"];
        $b = (int)$row["book_id"];
        $s = (float)$row["strength"];
        $key = $u . "_" . $b;

        if (!isset($interactionRows[$key])) {
            $interactionRows[$key] = [
                "user_id" => $u,
                "book_id" => $b,
                "strength" => 0.0
            ];
        }
        $interactionRows[$key]["strength"] += $s;
    }
}

$interactions = array_values($interactionRows);

$userBookMatrix = [];
foreach ($interactions as $row) {
    $u = (int)$row["user_id"];
    $b = (int)$row["book_id"];
    $s = (float)$row["strength"];

    if (!isset($userBookMatrix[$u])) {
        $userBookMatrix[$u] = [];
    }
    $userBookMatrix[$u][$b] = $s;
}

$collaborativeScores = [];

if (isset($userBookMatrix[$user_id])) {
    $targetVector = $userBookMatrix[$user_id];
    $similarUsers = [];

    foreach ($userBookMatrix as $otherUserId => $otherVector) {
        if ((int)$otherUserId === (int)$user_id) {
            continue;
        }

        $sim = cosine_similarity_assoc($targetVector, $otherVector);
        if ($sim > 0) {
            $similarUsers[(int)$otherUserId] = $sim;
        }
    }

    arsort($similarUsers);

    $userSeenBooks = array_keys($targetVector);

    foreach ($similarUsers as $otherUserId => $simScore) {
        $otherBooks = $userBookMatrix[$otherUserId] ?? [];

        foreach ($otherBooks as $bookId => $strength) {
            if (in_array((int)$bookId, $userSeenBooks, true)) {
                continue;
            }

            if (!isset($collaborativeScores[$bookId])) {
                $collaborativeScores[$bookId] = 0.0;
            }

            $collaborativeScores[$bookId] += ($simScore * $strength);
        }
    }
}

$collaborativeScores = normalize_scores($collaborativeScores);
$contentScores = normalize_scores($contentScores);

$finalScoredBooks = [];

foreach ($candidateBooks as $book) {
    $bookId = (int)$book["id"];

    if (isset($interactedBookIds[$bookId])) {
        continue;
    }

    $collab = $collaborativeScores[$bookId] ?? 0.0;
    $content = $contentScores[$bookId] ?? 0.0;

    $finalScore = (0.60 * $collab) + (0.40 * $content);

    $quantityBoost = min((int)($book["quantity"] ?? 0), 10) / 100.0;
    $popularityBoost = min((int)($book["popularity"] ?? 0), 20) / 200.0;

    $finalScore += $quantityBoost + $popularityBoost;

    $finalScoredBooks[] = [
        "id" => $bookId,
        "title" => $book["title"] ?? "",
        "author" => $book["author"] ?? "",
        "picture" => $book["picture"] ?? "",
        "category" => $book["category"] ?? "",
        "description" => $book["description"] ?? "",
        "release_date" => $book["release_date"] ?? null,
        "library_id" => (int)($book["library_id"] ?? 0),
        "quantity" => (int)($book["quantity"] ?? 0),
        "_score" => (float)$finalScore
    ];
}

usort($finalScoredBooks, function ($a, $b) {
    if ($a["_score"] == $b["_score"]) {
        $dateA = (string)($a["release_date"] ?? "");
        $dateB = (string)($b["release_date"] ?? "");

        if ($dateA === $dateB) {
            return $b["id"] <=> $a["id"];
        }
        return strcmp($dateB, $dateA);
    }

    return ($b["_score"] <=> $a["_score"]);
});

$recommendations = array_slice($finalScoredBooks, 0, $limit);

if (count($recommendations) < $limit) {
    $existingIds = [];
    foreach ($recommendations as $r) {
        $existingIds[(int)$r["id"]] = true;
    }

    $sqlFallback = "
        SELECT
            b.id,
            b.title,
            b.author,
            b.picture,
            b.category,
            b.description,
            b.release_date,
            ls.library_id,
            ls.quantity,
            COALESCE(tp.topic_count, 0) AS popularity
        FROM library_stock ls
        INNER JOIN books b ON b.id = ls.book_id
        LEFT JOIN (
            SELECT book_id, COUNT(*) AS topic_count
            FROM topics
            GROUP BY book_id
        ) tp ON tp.book_id = b.id
        WHERE ls.quantity > 0
        ORDER BY tp.topic_count DESC, b.release_date DESC, b.id DESC
    ";

    $resFallback = $conn->query($sqlFallback);
    $fallbackByBook = [];

    if ($resFallback) {
        while ($row = $resFallback->fetch_assoc()) {
            $bookId = (int)$row["id"];
            if (isset($existingIds[$bookId])) {
                continue;
            }

            if (!isset($fallbackByBook[$bookId])) {
                $fallbackByBook[$bookId] = [
                    "id" => $bookId,
                    "title" => $row["title"] ?? "",
                    "author" => $row["author"] ?? "",
                    "picture" => $row["picture"] ?? "",
                    "category" => $row["category"] ?? "",
                    "description" => $row["description"] ?? "",
                    "release_date" => $row["release_date"] ?? null,
                    "library_id" => (int)($row["library_id"] ?? 0),
                    "quantity" => (int)($row["quantity"] ?? 0),
                    "_score" => 0.0
                ];
            } else {
                if ((int)$row["quantity"] > (int)$fallbackByBook[$bookId]["quantity"]) {
                    $fallbackByBook[$bookId]["library_id"] = (int)($row["library_id"] ?? 0);
                    $fallbackByBook[$bookId]["quantity"] = (int)($row["quantity"] ?? 0);
                }
            }
        }
    }

    foreach ($fallbackByBook as $book) {
        if (count($recommendations) >= $limit) {
            break;
        }
        $recommendations[] = $book;
    }
}

$finalBooks = [];
foreach (array_slice($recommendations, 0, $limit) as $b) {
    $finalBooks[] = [
        "id" => (int)$b["id"],
        "title" => $b["title"],
        "author" => $b["author"],
        "picture" => $b["picture"],
        "category" => $b["category"],
        "description" => $b["description"] ?? "",
        "release_date" => $b["release_date"],
        "library_id" => (int)$b["library_id"],
        "quantity" => (int)$b["quantity"]
    ];
}

echo json_encode([
    "success" => true,
    "books" => $finalBooks
], JSON_UNESCAPED_UNICODE);

$conn->close();
?>