-- phpMyAdmin SQL Dump
-- version 5.2.1
-- https://www.phpmyadmin.net/
--
-- Gép: 127.0.0.1
-- Létrehozás ideje: 2026. Ápr 14. 11:11
-- Kiszolgáló verziója: 10.4.32-MariaDB
-- PHP verzió: 8.2.12

SET SQL_MODE = "NO_AUTO_VALUE_ON_ZERO";
START TRANSACTION;
SET time_zone = "+00:00";


/*!40101 SET @OLD_CHARACTER_SET_CLIENT=@@CHARACTER_SET_CLIENT */;
/*!40101 SET @OLD_CHARACTER_SET_RESULTS=@@CHARACTER_SET_RESULTS */;
/*!40101 SET @OLD_COLLATION_CONNECTION=@@COLLATION_CONNECTION */;
/*!40101 SET NAMES utf8mb4 */;

--
-- Adatbázis: `libraries`
--

-- --------------------------------------------------------

--
-- Tábla szerkezet ehhez a táblához `admins`
--

CREATE TABLE `admins` (
  `id` int(11) NOT NULL,
  `firstname` varchar(255) NOT NULL,
  `lastname` varchar(255) NOT NULL,
  `username` varchar(255) NOT NULL,
  `email` varchar(255) NOT NULL,
  `password` varchar(255) NOT NULL,
  `library_id` int(11) NOT NULL,
  `admin_code` varchar(255) NOT NULL
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci;

--
-- A tábla adatainak kiíratása `admins`
--

INSERT INTO `admins` (`id`, `firstname`, `lastname`, `username`, `email`, `password`, `library_id`, `admin_code`) VALUES
(1, 'Spongyabob', 'Kockanadrág', 'spongebob', 'spongebob@gmail.com', '$2y$10$UtqYiFvphTkaL2CrDq47ge6zv2iHTaCejEf2Wc7jK5CgwCgY0yMrO', 1, '55q7!I3y5R6w'),
(2, 'Atom', 'Anti', 'atom', 'atom@gmail.com', '$2y$10$c9BaOjyuXlaNanoGSU1i9.Cn88dvr2hhk99OyD3.e.7KLjzxxHyqy', 2, '3Z360B*yU$#$');

-- --------------------------------------------------------

--
-- Tábla szerkezet ehhez a táblához `books`
--

CREATE TABLE `books` (
  `id` int(11) NOT NULL,
  `title` varchar(255) NOT NULL,
  `author` varchar(255) NOT NULL,
  `picture` varchar(255) NOT NULL,
  `category` varchar(255) NOT NULL,
  `uploaded_by` varchar(255) NOT NULL,
  `description` varchar(1500) NOT NULL,
  `release_date` date NOT NULL
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci;

--
-- A tábla adatainak kiíratása `books`
--

INSERT INTO `books` (`id`, `title`, `author`, `picture`, `category`, `uploaded_by`, `description`, `release_date`) VALUES
(3, 'Harry Potter és a bölcsek köve', 'J.K. Rowling', '716048_5.jpg', 'Fantasy', 'spongebob', 'A Harry Potterről szóló, hétkötetesre tervezett regényfolyam első része. A könyvben megismerkedhetünk többek között a Roxfort varázslóiskolával, Harryvel, a varázslópalántával, és tanúi lehetünk csodálatosan izgalmas kalandjainak. \"Harry Potter kisfiú, történetünk kezdetén 11 éves, valamint varázsló is, talán a leghatalmasabb varázsló, a kiválasztott, aki meg tud küzdeni a gonosz erőivel, erről azonban fogalma sincs. (..) Harry aztán egy napon levelet kap, pontosabban néhány tízezer levelet, a biztonság kedvéért, mivel a nagybácsi elkobzós kedve magasra hág, amelyből megtudja, hogy a következő szemesztert Roxfortban kezdheti, a világ legnevesebb bentlakásos varázslóiskolájában, amely nem kis mértékben hasonlít a brit iskolarendszer hírhedett public schooljaira, talán attól eltekintve, hogy koedukált. Harry, a kifosztott árva ekkor belép abba a világba, amelyhez szülei is tartoztak, hogy megküzdjön azzal a Ki-Ne-Mondd-A-Nevét sötét erővel, amely árvává tette. Harry kiválasztott, homlokán a jegy, de egyben közönséges nebuló is, akinek minden kiválasztottsága ellenére fel kell mutatnia valamit, esetünkben kiemelkedő sportteljesítményt és kellő csapatszellemet, hogy elnyerje az egyszerű diáktársat megillető tiszteletet, és megússza valahogy a vizsgáit. A Gonosz Erőt nem könnyű legyőzni, de egy elitiskola hierarchiájában kiküzdeni valami helyet, főként, ha az alsóbbrendű muglik között nevelkedett az ember, és mit sem tud a magasabb bűbájról, az még nehezebb.', '1997-06-28'),
(4, 'Harry Potter és a titkok kamrája', 'J.K. Rowling', '716047_5.jpg', 'Fanatsy', 'spongebob', 'Könyvünk címszereplőjével, a varázslónak tanuló kiskamasszal már megismerkedhettünk a \"Harry Potter és a bölcsek köve\" című meseregényben. A mű és szerzője gyorsan világhírnévre tett szert. Harry varázslónak született. Második tanéve a Roxfort Boszorkány - és Varázslóképző Szakiskolában éppen olyan eseménydúsnak bizonyult, mint amilyen az első volt. Lekési a különvonatot, így barátai repülő autóján érkezik tanulmányai színhelyére. S a java csak ezután következik...', '1998-07-02'),
(5, 'Harry Potter és az azkabani fogoly', 'J.K. Rowling', '684283_5.jpg', 'Fantasy', 'spongebob', 'Azkabanból, a gonosz varázslókat őrző rettegett és szuperbiztos börtönből megszökik egy fogoly. A Mágiaügyi Minisztériumban tudják, hogy a veszélyes szökevény Roxfortba tart, a Boszorkány-és Varázslóképző Szakiskolába.A varázslónövendék Harry Potter és barátai számára a harmadik tanév sem csak a vizsgák izgalmait tartogatja...', '1999-07-08'),
(6, 'A ragyogás', 'Stephen King', '13028003_5.jpg', 'Horror', 'atom', ',,- Minden nagy szállodának vannak botrányai - mondta. - Ahogy kísértet is van minden nagy szállodában. Hogy miért? A fenébe is, az emberek jönnek-mennek. Megesik, hogy valamelyik a szobájában dobja fel a talpát, a szíve, vagy gutaütés, vagy valami ilyesmi. A szálloda babonás hely. Nincs tizenharmadik emelet, nincs tizenhármas szoba, nincs tükör az ajtón, amin az ember bemegy, meg hasonlók.\" Stephen King egyik leghíresebb története, A ragyogás, a Sziklás-hegység egyik magaslatán, egy világtól elzárt szállodában játszódik. Itt vállal állást az alkoholizmusából éppen kigyógyult Jack Torrance, és vele együtt ideköltözik felesége, Wendy, és kisfia, Danny is. Miközben Jack egyre megszállottabban próbálja megírni a szálloda történetét, természetfeletti, látnoki képességekkel rendelkező fia egyre több furcsa jelet lát... A ragyogás 1977-ben jelent meg először, és azonnal bestseller lett. Az azóta klasszikussá vált műből 1980-ban Stanley Kubrick rendezett filmet, Jack Nicholson főszereplésével.', '1977-01-28'),
(7, 'A hosszú menetelés', 'Stephen King', '12180283_5.jpg', 'Horror', 'atom', 'Stephen King több mint hatvan bestseller szerzője, számos rangos irodalmi díj tulajdonosa. Könyvei közül a Csak sötéten szereted felkerült 2024 tíz legjobb horrorkönyvének, a Holly a 2023-as év legfigyelemreméltóbb könyveinek, a 11/22/63 pedig a 2011-es év legjobb tíz könyvének listájára a The New York Times Book Review szerkesztői szerint. King számos más elismerése mellett a 2018-as PEN America irodalmi díj, valamint az Egyesült Államok elnöke által adományozott legmagasabb művészeti kitüntetés, a National Medal of Arts 2014-es birtokosa. A Maine állambeli Bangorben él feleségével, Tabitha King regényíróval.\nEgy nem is olyan távoli, disztópikus jövőben Amerika kegyetlen időszak elé néz. A tizenhat éves Ray Garraty útnak indul, hogy részt vegyen a mentális és fizikai állóképesség legnagyobb próbatételén, az embertelen versenyen, a \"Hosszú Menetelés\"-en, amely évente megrendezésre kerül. Száz fiú éjjel-nappal egyenletes, óránként hat kilométeres tempóban menetel, megállás nélkül. A \"díj\" nem más, mint a győztes álma: bármi legyen is az, megkapja, élete végéig. A szabályok szigorúak, a tét óriási. Nincs célvonal: az utolsó talpon maradó ember nyer. A versenyzők semmilyen külső segítséget nem kaphatnak. Ha bárki lassít, nem tudja tartani a tempót, figyelmeztetésben részesül. Három figyelmeztetés kieséssel jár, örökre.', '1979-07-10'),
(8, 'Kedvencek temetője', 'Stephen King', '5133250_5.jpg', 'Horror', 'atom', 'Dr. Louis Creed, a fiatal orvos kitűnő állást kapott: a Maine-i Egyetem rendelőjének lett a vezetője, ezért Chicagóból az idilli New England-i tájban álló, magányos házba költözik családjával - feleségével, Rachellel, ötéves lányukkal, Ellie-vel és másfél éves kisfiukkal, Gage-dzsel. Boldogan, a szép jövő reményében veszik birtokukba új otthonukat...\nAz első gondra az út túloldalán, velük átellenben élő öregember, Jud hívja föl a figyelmüket: a tájat kettészelő országúton éjjel-nappal olajszállító tartálykocsik dübörögnek, halálos veszélynek téve ki a háziállatokat és az apróságokat. Nem véletlenül van a közelben egy nyomasztó légkörű, ódon temető az elgázolt háziállatok számára... Az első trauma akkor éri Louist, amikor egy baleset áldozatául esett, haldokló fiú a rendelőben dadogó szavakkal óva inti az állattemetőn túli veszedelemtől. Nem sokra rá egy tartálykocsi elgázolja Ellie imádott macskáját, és az öreg Jud - jó- vagy rosszakaratból? - az állattemetőn túli, hátborzongató vidékre, a micmac indiánok egykori temetkezőhelyére viszi Louist, s ott földelteti el vele az állatot.\nMásnap a macska visszatér - de ocsmány jószág lett belőle: lomha, ijesztően bűzlő és gonosz. Aztán néhány békés hónap után a kis Gage elszabadul a szüleitől, és szaladni kezd pici lábain az országút felé...', '1983-11-14'),
(9, 'A búra alatt', 'Stephen King', '9268322_5.jpg', 'Horror', 'atom', 'Egy teljesen átlagos, gyönyörű őszi napon a békés maine-i Chester\'s Mill kisvárosát váratlanul, a derült égből egy láthatatlan erőtér zárja el a világ többi részétől. A hirtelen alászálló \"búrába\" belecsapódik egy repülőgép és lángoló roncsokban zuhan le az égből, egy kertészkedő asszony keze leszakad, a szomszédos városban dolgozó emberek nem tudnak hazajutni. Senki sem érti, mi ez a megmagyarázhatatlan jelenség, honnan jött, és mikor tűnik el, ha egyáltalán. Aztán ahogy az emberek lassan felfogják, hogy a láthatatlan, de nagyon is valóságos akadály ott van és ott marad, az is kiderül, számukra, hogy Chester\'s Mill nem is olyan békés hely.\nDale Barbara, a városban rekedt iraki veterán és alkalmi szakács az események közepén találja magát. Néhány rettenthetetlen ottani lakóval - a helyi újság tulajdonosával, Julia Shumwayjel, két lelkésszel, egy kórházi asszisztenssel, egy anyával és három bátor gyerekkel - kénytelen szembeszállni a város vezetését átvevő Big Jim Rennie-vel, aki még a gyilkosságtól sem riad vissza annak érdekében, hogy kézben tartsa a gyeplőt. Mindeközben a politikus erőszakos fia szörnyű titkokat őriz egy ház sötét kamrájában. De nincs mód elmenekülni a városból, az idő és a levegő pedig egyre kevesebb...', '2009-11-10'),
(10, 'Csontkollekció', 'Stephen King', '1000000036.jpg', 'Horror', 'spongebob', 'Mindig érhetik meglepetések az embert... Békésen pihen tóparti nyaralójában családjával, amikor fergetes vihar kerekedik, fákat csavar ki tövestül, leszaggatja a tetőt. Kitombolja magát, majd elvonul. És akkor a tó fölött leszáll a köd. Egyre közeledik, mindent beborít, és valamilyen furcsa csápok, nyúlványok, tapadókorongok kúsznak elő belőle, és amit megragadnak... Szóval nem árt meghallgatni az időjárás-jelentést! A klubban kártyázik a barátaival, de hiányzik egy játékos. Sebaj, az ott ülő jól nevelt idegen felajánlja, hogy beszáll a partiba. Csakhogy az úriember nem hajlandó kezet fogni bemutatkozáskor. Ez az egyszerű udvariassági gesztus ugyanis végzetes következményekkel járhat. Ne erőltessük! Jobb a békesség! Régi kacatok között talál egy játék majmot. A tengerész papa hozta tán külföldről, fura egy jószág - és milyen ocsmányul vigyorog! És hogy került a padlásra, holott már évekkel azelőtt kidobták?! Miért tér mindig vissza? Megrendeli a tejet, narancslevet stb., hisz milyen jó is az, ha házhoz szállítják a reggelit! Hajnalban jön a pofás kis teherautó, a tejesember meg lerakja az ajtó elé az árut... ja, hogy esetleg földúsította az üvegek tartalmát? Talán mégis jobb, ha elugrunk a boltba! Hajótörést szenved, lakatlan szigetre vetődik. Néhány alapvető dolgot sikerül megmentenie, például ceruzát, kést, varrókészletet meg két kiló heroint... A sziget olyannyira kopár, hogy legfeljebb egy-két sirály száll le rá, de nagyon nehéz ám elkapni a fürge jószágot, pláne úgy, hog', '1985-06-21'),
(11, 'Harry Potter és a Tűz Serlege', 'J.K. Rowling', '1000000037.jpg', 'Fantasy', 'spongebob', 'Melyik nemzeti válogatott nyeri a Kviddics Világkupát? Ki lesz a Trimágus Tusa győztese? Utóbbiért a világ három boszorkány- és varázslóképző tanintézetének legrátermettebb diákjai küzdenek. A világraszóló versengés házigazdája Roxfort, az az iskola, ahová Harry Potter immár negyedévesként érkezik. S ahogy az a felsőbb osztályosoknál már egyáltalán nem különös, Harry és barátai a másik nemet is felfedezik... Ám nem csupán e kellemes izgalmakat ígérő események várnak Harryre és barátaira. Voldemort, a fekete mágusok vezére újból készülődik...Tele van a történet váratlanokkal, véletlenekkel, s miért tagadnánk: rémekkel, szörnyekkel, kísértetekkel. Valahogy annyira tele, mint az életünk.', '2000-07-08'),
(15, 'Démonmester - A vérükben van', 'Darren Shan', '98c0e45d8aee198ecb7194d1d171bc01_big.jpg', 'Horror', 'spongebob', 'Amikor Grubbs Grady először áll szemtől szembe Vész herceggel és förtelmes csatlósaival, három dolgot tanul meg: a világ gonosz, a varázslat lehetséges, démonok léteznek. Úgy gondolja, soha többé nem lesz részese ilyen iszonytató eseményeknek, mint ezen a koromsötét, halálszagú éjszakán. Nagyot téved. Darren Shan, a nagy sikerű Vámpír Könyvek szerzője új sorozatában a Démonvilág borzalmait ecseteli nagy átéléssel. Az első kötet tizenhárom éves főhősének arra a kérdésre kell megtalálnia a választ: mi a jobb: farkasemberré válni, vagy szembeszállni a nagy hatalmú démonmesterrel.', '2005-06-06');

-- --------------------------------------------------------

--
-- Tábla szerkezet ehhez a táblához `comments`
--

CREATE TABLE `comments` (
  `id` int(11) NOT NULL,
  `topic_id` int(11) NOT NULL,
  `user_id` int(11) NOT NULL,
  `comment` varchar(255) NOT NULL,
  `created_at` date NOT NULL,
  `username` varchar(255) NOT NULL
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci;

-- --------------------------------------------------------

--
-- Tábla szerkezet ehhez a táblához `events`
--

CREATE TABLE `events` (
  `id` int(11) NOT NULL,
  `title` varchar(255) NOT NULL,
  `header` varchar(255) NOT NULL,
  `picture` varchar(255) NOT NULL,
  `description` varchar(500) NOT NULL,
  `date` date NOT NULL,
  `library_id` int(11) NOT NULL,
  `admin_id` int(11) NOT NULL
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci;

--
-- A tábla adatainak kiíratása `events`
--

INSERT INTO `events` (`id`, `title`, `header`, `picture`, `description`, `date`, `library_id`, `admin_id`) VALUES
(4, 'Test', 'This is a test', '1000000035.jpg', 'Just a test', '2026-04-16', 2, 2),
(5, 'TEST', 'This is a test', 'canstockphoto22402523-arcos-creator.com_-1024x1024-1.jpg', 'Just a test', '2026-05-05', 1, 1),
(6, 'Haha', 'Test', 'canstockphoto22402523-arcos-creator.com_-1024x1024-1.jpg', 'Just a test', '2026-10-10', 1, 1);

-- --------------------------------------------------------

--
-- Tábla szerkezet ehhez a táblához `library`
--

CREATE TABLE `library` (
  `id` int(11) NOT NULL,
  `name` varchar(255) NOT NULL,
  `city` varchar(255) NOT NULL,
  `picture` varchar(255) NOT NULL
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci;

--
-- A tábla adatainak kiíratása `library`
--

INSERT INTO `library` (`id`, `name`, `city`, `picture`) VALUES
(1, 'Bródy Sándor Megyei és Városi Könyvtár', 'Eger', 'brody_sandor.jpg'),
(2, 'Hild Viktor Városi Könyvtár', 'Szolnok', 'hild_viktor.jpg');

-- --------------------------------------------------------

--
-- Tábla szerkezet ehhez a táblához `library_stock`
--

CREATE TABLE `library_stock` (
  `id` int(11) NOT NULL,
  `library_id` int(11) NOT NULL,
  `book_id` int(11) NOT NULL,
  `quantity` int(11) NOT NULL
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci;

--
-- A tábla adatainak kiíratása `library_stock`
--

INSERT INTO `library_stock` (`id`, `library_id`, `book_id`, `quantity`) VALUES
(3, 1, 3, 24),
(4, 1, 4, 20),
(5, 1, 5, 14),
(6, 2, 6, 33),
(7, 2, 7, 7),
(8, 2, 8, 15),
(9, 2, 9, 23),
(10, 1, 10, 9),
(11, 1, 11, 13),
(12, 1, 15, 13);

-- --------------------------------------------------------

--
-- Tábla szerkezet ehhez a táblához `on_lease`
--

CREATE TABLE `on_lease` (
  `id` int(11) NOT NULL,
  `user_id` int(11) NOT NULL,
  `library_id` int(11) NOT NULL,
  `book_id` int(11) NOT NULL,
  `leased_date` timestamp NOT NULL DEFAULT current_timestamp() ON UPDATE current_timestamp(),
  `returned_date` timestamp NULL DEFAULT NULL
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci;

--
-- A tábla adatainak kiíratása `on_lease`
--

INSERT INTO `on_lease` (`id`, `user_id`, `library_id`, `book_id`, `leased_date`, `returned_date`) VALUES
(3, 1, 1, 3, '2026-04-09 08:02:28', '2026-04-09 08:02:28'),
(4, 1, 1, 3, '2026-04-09 08:02:38', '2026-04-09 08:02:38'),
(5, 1, 1, 3, '2026-04-09 18:53:24', '2026-04-09 18:53:24'),
(6, 1, 2, 8, '2026-04-10 07:22:13', '2026-04-10 07:22:13'),
(7, 1, 1, 5, '2026-04-12 11:11:14', '2026-04-12 11:11:14'),
(8, 1, 2, 9, '2026-04-14 08:01:26', '2026-04-14 08:01:26'),
(9, 1, 1, 11, '2026-04-14 08:17:23', '2026-04-14 08:17:23');

-- --------------------------------------------------------

--
-- Tábla szerkezet ehhez a táblához `topics`
--

CREATE TABLE `topics` (
  `id` int(11) NOT NULL,
  `topic` varchar(255) NOT NULL,
  `description` varchar(500) NOT NULL,
  `rating` int(11) NOT NULL,
  `book_id` int(11) NOT NULL,
  `user_id` int(11) NOT NULL
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci;

-- --------------------------------------------------------

--
-- Tábla szerkezet ehhez a táblához `users`
--

CREATE TABLE `users` (
  `id` int(11) NOT NULL,
  `lastname` varchar(255) NOT NULL,
  `firstname` varchar(255) NOT NULL,
  `username` varchar(255) NOT NULL,
  `email` varchar(255) NOT NULL,
  `password` varchar(255) NOT NULL
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci;

--
-- A tábla adatainak kiíratása `users`
--

INSERT INTO `users` (`id`, `lastname`, `firstname`, `username`, `email`, `password`) VALUES
(1, 'Bálint', 'Katona', 'katbal', 'katbal@gmail.com', '$2y$10$WLNJlQbtEjt.Jl9UdC9fu.KOe67Qe0LoTl1u9PFg2Ktp7XbMhh0v6'),
(2, 'Elek', 'Mek', 'mekelek', 'mekelek@gmail.com', '$2b$12$bRrgNDp8IRqt.c2I0HgV5OfBhTwe2QoX1zfPpiBaDRiEHKHkP74u.');

--
-- Indexek a kiírt táblákhoz
--

--
-- A tábla indexei `admins`
--
ALTER TABLE `admins`
  ADD PRIMARY KEY (`id`),
  ADD KEY `id` (`id`),
  ADD KEY `library_id` (`library_id`);

--
-- A tábla indexei `books`
--
ALTER TABLE `books`
  ADD PRIMARY KEY (`id`),
  ADD KEY `id` (`id`);

--
-- A tábla indexei `comments`
--
ALTER TABLE `comments`
  ADD PRIMARY KEY (`id`),
  ADD KEY `id` (`id`),
  ADD KEY `topic_id` (`topic_id`,`user_id`),
  ADD KEY `comments_user_id_fk` (`user_id`);

--
-- A tábla indexei `events`
--
ALTER TABLE `events`
  ADD PRIMARY KEY (`id`),
  ADD KEY `id` (`id`),
  ADD KEY `library_id` (`library_id`,`admin_id`),
  ADD KEY `events_admin_id_fk` (`admin_id`);

--
-- A tábla indexei `library`
--
ALTER TABLE `library`
  ADD PRIMARY KEY (`id`),
  ADD KEY `id` (`id`);

--
-- A tábla indexei `library_stock`
--
ALTER TABLE `library_stock`
  ADD PRIMARY KEY (`id`),
  ADD KEY `library_id` (`library_id`,`book_id`),
  ADD KEY `library_stock_book_id_fk` (`book_id`);

--
-- A tábla indexei `on_lease`
--
ALTER TABLE `on_lease`
  ADD PRIMARY KEY (`id`),
  ADD KEY `user_id` (`user_id`,`library_id`,`book_id`),
  ADD KEY `on_lease_library_id_fk` (`library_id`),
  ADD KEY `on_lease_book_id_fk` (`book_id`);

--
-- A tábla indexei `topics`
--
ALTER TABLE `topics`
  ADD PRIMARY KEY (`id`),
  ADD KEY `id` (`id`),
  ADD KEY `book_id` (`book_id`,`user_id`),
  ADD KEY `topics_user_id_fk` (`user_id`);

--
-- A tábla indexei `users`
--
ALTER TABLE `users`
  ADD PRIMARY KEY (`id`),
  ADD KEY `id` (`id`),
  ADD KEY `id_2` (`id`),
  ADD KEY `id_3` (`id`);

--
-- A kiírt táblák AUTO_INCREMENT értéke
--

--
-- AUTO_INCREMENT a táblához `admins`
--
ALTER TABLE `admins`
  MODIFY `id` int(11) NOT NULL AUTO_INCREMENT, AUTO_INCREMENT=3;

--
-- AUTO_INCREMENT a táblához `books`
--
ALTER TABLE `books`
  MODIFY `id` int(11) NOT NULL AUTO_INCREMENT, AUTO_INCREMENT=16;

--
-- AUTO_INCREMENT a táblához `comments`
--
ALTER TABLE `comments`
  MODIFY `id` int(11) NOT NULL AUTO_INCREMENT, AUTO_INCREMENT=13;

--
-- AUTO_INCREMENT a táblához `events`
--
ALTER TABLE `events`
  MODIFY `id` int(11) NOT NULL AUTO_INCREMENT, AUTO_INCREMENT=7;

--
-- AUTO_INCREMENT a táblához `library`
--
ALTER TABLE `library`
  MODIFY `id` int(11) NOT NULL AUTO_INCREMENT, AUTO_INCREMENT=3;

--
-- AUTO_INCREMENT a táblához `library_stock`
--
ALTER TABLE `library_stock`
  MODIFY `id` int(11) NOT NULL AUTO_INCREMENT, AUTO_INCREMENT=13;

--
-- AUTO_INCREMENT a táblához `on_lease`
--
ALTER TABLE `on_lease`
  MODIFY `id` int(11) NOT NULL AUTO_INCREMENT, AUTO_INCREMENT=10;

--
-- AUTO_INCREMENT a táblához `topics`
--
ALTER TABLE `topics`
  MODIFY `id` int(11) NOT NULL AUTO_INCREMENT, AUTO_INCREMENT=9;

--
-- AUTO_INCREMENT a táblához `users`
--
ALTER TABLE `users`
  MODIFY `id` int(11) NOT NULL AUTO_INCREMENT, AUTO_INCREMENT=3;

--
-- Megkötések a kiírt táblákhoz
--

--
-- Megkötések a táblához `admins`
--
ALTER TABLE `admins`
  ADD CONSTRAINT `admin_library_id_fk` FOREIGN KEY (`library_id`) REFERENCES `library` (`id`);

--
-- Megkötések a táblához `comments`
--
ALTER TABLE `comments`
  ADD CONSTRAINT `comments_topic_id_fk` FOREIGN KEY (`topic_id`) REFERENCES `topics` (`id`),
  ADD CONSTRAINT `comments_user_id_fk` FOREIGN KEY (`user_id`) REFERENCES `users` (`id`);

--
-- Megkötések a táblához `events`
--
ALTER TABLE `events`
  ADD CONSTRAINT `events_admin_id_fk` FOREIGN KEY (`admin_id`) REFERENCES `admins` (`id`),
  ADD CONSTRAINT `events_library_id_fk` FOREIGN KEY (`library_id`) REFERENCES `library` (`id`);

--
-- Megkötések a táblához `library_stock`
--
ALTER TABLE `library_stock`
  ADD CONSTRAINT `library_stock_book_id_fk` FOREIGN KEY (`book_id`) REFERENCES `books` (`id`),
  ADD CONSTRAINT `library_stock_library_id_fk` FOREIGN KEY (`library_id`) REFERENCES `library` (`id`);

--
-- Megkötések a táblához `on_lease`
--
ALTER TABLE `on_lease`
  ADD CONSTRAINT `on_lease_book_id_fk` FOREIGN KEY (`book_id`) REFERENCES `books` (`id`),
  ADD CONSTRAINT `on_lease_library_id_fk` FOREIGN KEY (`library_id`) REFERENCES `library` (`id`),
  ADD CONSTRAINT `on_lease_user_id_fk` FOREIGN KEY (`user_id`) REFERENCES `users` (`id`);

--
-- Megkötések a táblához `topics`
--
ALTER TABLE `topics`
  ADD CONSTRAINT `topics_book_id_fk` FOREIGN KEY (`book_id`) REFERENCES `books` (`id`),
  ADD CONSTRAINT `topics_user_id_fk` FOREIGN KEY (`user_id`) REFERENCES `users` (`id`);
COMMIT;

/*!40101 SET CHARACTER_SET_CLIENT=@OLD_CHARACTER_SET_CLIENT */;
/*!40101 SET CHARACTER_SET_RESULTS=@OLD_CHARACTER_SET_RESULTS */;
/*!40101 SET COLLATION_CONNECTION=@OLD_COLLATION_CONNECTION */;
