-- phpMyAdmin SQL Dump
-- version 4.8.5
-- https://www.phpmyadmin.net/
--
-- Host: 127.0.0.1
-- Generation Time: Jul 02, 2019 at 01:13 PM
-- Server version: 10.1.39-MariaDB
-- PHP Version: 7.3.5

SET SQL_MODE = "NO_AUTO_VALUE_ON_ZERO";
SET AUTOCOMMIT = 0;
START TRANSACTION;
SET time_zone = "+00:00";


/*!40101 SET @OLD_CHARACTER_SET_CLIENT=@@CHARACTER_SET_CLIENT */;
/*!40101 SET @OLD_CHARACTER_SET_RESULTS=@@CHARACTER_SET_RESULTS */;
/*!40101 SET @OLD_COLLATION_CONNECTION=@@COLLATION_CONNECTION */;
/*!40101 SET NAMES utf8mb4 */;

--
-- Database: `fpdatabase`
--

-- --------------------------------------------------------

--
-- Table structure for table `fpdata`
--

CREATE TABLE `fpdata` (
  `counter` int(11) NOT NULL,
  `id` varchar(50) COLLATE utf8_unicode_ci DEFAULT NULL,
  `os` varchar(50) COLLATE utf8_unicode_ci DEFAULT NULL,
  `browser` varchar(50) COLLATE utf8_unicode_ci DEFAULT NULL,
  `time` datetime NOT NULL,
  `userAgentHttp` varchar(300) COLLATE utf8_unicode_ci DEFAULT NULL,
  `acceptHttp` varchar(300) COLLATE utf8_unicode_ci DEFAULT NULL,
  `encodingHttp` varchar(200) COLLATE utf8_unicode_ci DEFAULT NULL,
  `languageHttp` varchar(200) COLLATE utf8_unicode_ci NOT NULL,
  `orderHttp` varchar(200) COLLATE utf8_unicode_ci NOT NULL,
  `pluginsJS` text COLLATE utf8_unicode_ci NOT NULL,
  `platformJS` varchar(50) COLLATE utf8_unicode_ci DEFAULT NULL,
  `cookiesJS` varchar(10) COLLATE utf8_unicode_ci NOT NULL,
  `dntJS` varchar(10) COLLATE utf8_unicode_ci NOT NULL,
  `timezoneJS` varchar(10) COLLATE utf8_unicode_ci NOT NULL,
  `resolutionJS` varchar(20) COLLATE utf8_unicode_ci NOT NULL,
  `localJS` varchar(10) COLLATE utf8_unicode_ci NOT NULL,
  `sessionJS` varchar(10) COLLATE utf8_unicode_ci NOT NULL,
  `canvasJS` longtext COLLATE utf8_unicode_ci,
  `detectedFonts` mediumtext COLLATE utf8_unicode_ci,
  `fontsJS` varchar(200) COLLATE utf8_unicode_ci NOT NULL,
  `adBlock` varchar(10) COLLATE utf8_unicode_ci NOT NULL,
  `vendorWebGLJS` varchar(200) COLLATE utf8_unicode_ci NOT NULL,
  `rendererWebGLJS` varchar(200) COLLATE utf8_unicode_ci NOT NULL
) ENGINE=InnoDB DEFAULT CHARSET=utf8 COLLATE=utf8_unicode_ci;

--
-- Indexes for dumped tables
--

--
-- Indexes for table `fpdata`
--
ALTER TABLE `fpdata`
  ADD PRIMARY KEY (`counter`),
  ADD KEY `userAgentHttp` (`userAgentHttp`(255)),
  ADD KEY `localJS` (`localJS`),
  ADD KEY `dntJS` (`dntJS`),
  ADD KEY `os` (`os`),
  ADD KEY `browser` (`browser`),
  ADD KEY `acceptHttp` (`acceptHttp`(255)),
  ADD KEY `encodingHttp` (`encodingHttp`),
  ADD KEY `languageHttp` (`languageHttp`),
  ADD KEY `orderHttp` (`orderHttp`),
  ADD KEY `platformJS` (`platformJS`),
  ADD KEY `cookiesJS` (`cookiesJS`),
  ADD KEY `timezoneJS` (`timezoneJS`),
  ADD KEY `resolutionJS` (`resolutionJS`),
  ADD KEY `sessionJS` (`sessionJS`),
  ADD KEY `adBlock` (`adBlock`),
  ADD KEY `fontsJS` (`fontsJS`),
  ADD KEY `vendorWebGLJS` (`vendorWebGLJS`),
  ADD KEY `rendererWebGLJS` (`rendererWebGLJS`);

--
-- AUTO_INCREMENT for dumped tables
--

--
-- AUTO_INCREMENT for table `fpdata`
--
ALTER TABLE `fpdata`
  MODIFY `counter` int(11) NOT NULL AUTO_INCREMENT, AUTO_INCREMENT=0;
COMMIT;

/*!40101 SET CHARACTER_SET_CLIENT=@OLD_CHARACTER_SET_CLIENT */;
/*!40101 SET CHARACTER_SET_RESULTS=@OLD_CHARACTER_SET_RESULTS */;
/*!40101 SET COLLATION_CONNECTION=@OLD_COLLATION_CONNECTION */;
