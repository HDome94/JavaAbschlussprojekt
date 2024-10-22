-- phpMyAdmin SQL Dump
-- version 5.2.1
-- https://www.phpmyadmin.net/
--
-- Host: 127.0.0.1
-- Erstellungszeit: 11. Okt 2024 um 10:17
-- Server-Version: 10.4.32-MariaDB
-- PHP-Version: 8.2.12

SET SQL_MODE = "NO_AUTO_VALUE_ON_ZERO";
START TRANSACTION;
SET time_zone = "+00:00";


/*!40101 SET @OLD_CHARACTER_SET_CLIENT=@@CHARACTER_SET_CLIENT */;
/*!40101 SET @OLD_CHARACTER_SET_RESULTS=@@CHARACTER_SET_RESULTS */;
/*!40101 SET @OLD_COLLATION_CONNECTION=@@COLLATION_CONNECTION */;
/*!40101 SET NAMES utf8mb4 */;

--
-- Datenbank: `solarverwaltung`
--
CREATE DATABASE IF NOT EXISTS `solarverwaltung` DEFAULT CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci;
USE `solarverwaltung`;

-- --------------------------------------------------------

--
-- Tabellenstruktur für Tabelle `addresses`
--

CREATE TABLE `addresses` (
  `Id` bigint(20) NOT NULL,
  `Street` varchar(100) NOT NULL,
  `HouseNr` varchar(10) NOT NULL,
  `CityID` bigint(20) NOT NULL
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci;

-- --------------------------------------------------------

--
-- Tabellenstruktur für Tabelle `batteries`
--

CREATE TABLE `batteries` (
  `Id` bigint(20) NOT NULL,
  `Brand` varchar(50) NOT NULL,
  `Model` varchar(50) NOT NULL,
  `Capacity` double NOT NULL,
  `Charge` double NOT NULL,
  `SystemID` bigint(20) NOT NULL
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci;

-- --------------------------------------------------------

--
-- Tabellenstruktur für Tabelle `cities`
--

CREATE TABLE `cities` (
  `Id` bigint(20) NOT NULL,
  `PostalCode` varchar(10) NOT NULL,
  `City` varchar(50) NOT NULL
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci;

-- --------------------------------------------------------

--
-- Tabellenstruktur für Tabelle `electricitysales`
--

CREATE TABLE `electricitysales` (
  `Id` bigint(20) NOT NULL,
  `DateTime` datetime NOT NULL,
  `Watt` double NOT NULL,
  `IsPurchased` tinyint(1) NOT NULL,
  `PricePerKW` double NOT NULL,
  `SystemID` bigint(20) NOT NULL
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci;

-- --------------------------------------------------------

--
-- Tabellenstruktur für Tabelle `measurements`
--

CREATE TABLE `measurements` (
  `Id` bigint(20) NOT NULL,
  `DateTime` datetime NOT NULL,
  `Watt` double NOT NULL,
  `isProduced` tinyint(1) NOT NULL,
  `SystemID` bigint(20) NOT NULL
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci;

-- --------------------------------------------------------

--
-- Tabellenstruktur für Tabelle `solarpanels`
--

CREATE TABLE `solarpanels` (
  `Id` bigint(20) NOT NULL,
  `Brand` varchar(50) NOT NULL,
  `Model` varchar(50) NOT NULL,
  `MaximumWatt` int(11) NOT NULL,
  `SystemID` bigint(20) NOT NULL
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci;

-- --------------------------------------------------------

--
-- Tabellenstruktur für Tabelle `solarsystems`
--

CREATE TABLE `solarsystems` (
  `Id` bigint(20) NOT NULL,
  `Direction` enum('N','NE','E','SE','S','SW','W','NW') NOT NULL,
  `AddressID` bigint(20) NOT NULL
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci;

--
-- Indizes der exportierten Tabellen
--

--
-- Indizes für die Tabelle `addresses`
--
ALTER TABLE `addresses`
  ADD PRIMARY KEY (`Id`),
  ADD KEY `CityID` (`CityID`);

--
-- Indizes für die Tabelle `batteries`
--
ALTER TABLE `batteries`
  ADD PRIMARY KEY (`Id`),
  ADD KEY `SystemID` (`SystemID`);

--
-- Indizes für die Tabelle `cities`
--
ALTER TABLE `cities`
  ADD PRIMARY KEY (`Id`);

--
-- Indizes für die Tabelle `electricitysales`
--
ALTER TABLE `electricitysales`
  ADD PRIMARY KEY (`Id`),
  ADD KEY `SystemID` (`SystemID`);

--
-- Indizes für die Tabelle `measurements`
--
ALTER TABLE `measurements`
  ADD PRIMARY KEY (`Id`),
  ADD KEY `SystemID` (`SystemID`);

--
-- Indizes für die Tabelle `solarpanels`
--
ALTER TABLE `solarpanels`
  ADD PRIMARY KEY (`Id`),
  ADD KEY `SystemID` (`SystemID`);

--
-- Indizes für die Tabelle `solarsystems`
--
ALTER TABLE `solarsystems`
  ADD PRIMARY KEY (`Id`),
  ADD KEY `AddressID` (`AddressID`);

--
-- AUTO_INCREMENT für exportierte Tabellen
--

--
-- AUTO_INCREMENT für Tabelle `addresses`
--
ALTER TABLE `addresses`
  MODIFY `Id` bigint(20) NOT NULL AUTO_INCREMENT;

--
-- AUTO_INCREMENT für Tabelle `batteries`
--
ALTER TABLE `batteries`
  MODIFY `Id` bigint(20) NOT NULL AUTO_INCREMENT;

--
-- AUTO_INCREMENT für Tabelle `cities`
--
ALTER TABLE `cities`
  MODIFY `Id` bigint(20) NOT NULL AUTO_INCREMENT;

--
-- AUTO_INCREMENT für Tabelle `electricitysales`
--
ALTER TABLE `electricitysales`
  MODIFY `Id` bigint(20) NOT NULL AUTO_INCREMENT;

--
-- AUTO_INCREMENT für Tabelle `measurements`
--
ALTER TABLE `measurements`
  MODIFY `Id` bigint(20) NOT NULL AUTO_INCREMENT;

--
-- AUTO_INCREMENT für Tabelle `solarpanels`
--
ALTER TABLE `solarpanels`
  MODIFY `Id` bigint(20) NOT NULL AUTO_INCREMENT;

--
-- AUTO_INCREMENT für Tabelle `solarsystems`
--
ALTER TABLE `solarsystems`
  MODIFY `Id` bigint(20) NOT NULL AUTO_INCREMENT;

--
-- Constraints der exportierten Tabellen
--

--
-- Constraints der Tabelle `addresses`
--
ALTER TABLE `addresses`
  ADD CONSTRAINT `addresses_ibfk_1` FOREIGN KEY (`CityID`) REFERENCES `cities` (`Id`) ON UPDATE CASCADE;

--
-- Constraints der Tabelle `batteries`
--
ALTER TABLE `batteries`
  ADD CONSTRAINT `batteries_ibfk_1` FOREIGN KEY (`SystemID`) REFERENCES `solarsystems` (`Id`) ON DELETE CASCADE ON UPDATE CASCADE;

--
-- Constraints der Tabelle `electricitysales`
--
ALTER TABLE `electricitysales`
  ADD CONSTRAINT `electricitysales_ibfk_1` FOREIGN KEY (`SystemID`) REFERENCES `solarsystems` (`Id`) ON DELETE CASCADE ON UPDATE CASCADE;

--
-- Constraints der Tabelle `measurements`
--
ALTER TABLE `measurements`
  ADD CONSTRAINT `measurements_ibfk_1` FOREIGN KEY (`SystemID`) REFERENCES `solarsystems` (`Id`) ON DELETE CASCADE ON UPDATE CASCADE;

--
-- Constraints der Tabelle `solarpanels`
--
ALTER TABLE `solarpanels`
  ADD CONSTRAINT `solarpanels_ibfk_1` FOREIGN KEY (`SystemID`) REFERENCES `solarsystems` (`Id`) ON DELETE CASCADE ON UPDATE CASCADE;

--
-- Constraints der Tabelle `solarsystems`
--
ALTER TABLE `solarsystems`
  ADD CONSTRAINT `solarsystems_ibfk_1` FOREIGN KEY (`AddressID`) REFERENCES `addresses` (`Id`) ON UPDATE CASCADE;
COMMIT;

/*!40101 SET CHARACTER_SET_CLIENT=@OLD_CHARACTER_SET_CLIENT */;
/*!40101 SET CHARACTER_SET_RESULTS=@OLD_CHARACTER_SET_RESULTS */;
/*!40101 SET COLLATION_CONNECTION=@OLD_COLLATION_CONNECTION */;
