-- phpMyAdmin SQL Dump
-- version 5.2.1
-- https://www.phpmyadmin.net/
--
-- Host: 127.0.0.1
-- Generation Time: Apr 25, 2025 at 05:09 AM
-- Server version: 10.4.32-MariaDB
-- PHP Version: 8.1.25

SET SQL_MODE = "NO_AUTO_VALUE_ON_ZERO";
START TRANSACTION;
SET time_zone = "+00:00";


/*!40101 SET @OLD_CHARACTER_SET_CLIENT=@@CHARACTER_SET_CLIENT */;
/*!40101 SET @OLD_CHARACTER_SET_RESULTS=@@CHARACTER_SET_RESULTS */;
/*!40101 SET @OLD_COLLATION_CONNECTION=@@COLLATION_CONNECTION */;
/*!40101 SET NAMES utf8mb4 */;

--
-- Database: `hitchx`
--

-- --------------------------------------------------------

--
-- Table structure for table `accountdetails`
--

CREATE TABLE `accountdetails` (
  `accUsername` varchar(251) NOT NULL,
  `accPassword` varchar(251) NOT NULL
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci;

--
-- Dumping data for table `accountdetails`
--

INSERT INTO `accountdetails` (`accUsername`, `accPassword`) VALUES
('12', '12'),
('ahron12', 'badili3'),
('delxt', 'password'),
('delxt00', 'password'),
('jed', 'jedpass'),
('Username', 'Password');

-- --------------------------------------------------------

--
-- Table structure for table `deliveryform`
--

CREATE TABLE `deliveryform` (
  `Customername` varchar(251) NOT NULL,
  `Numberofcarton` varchar(251) NOT NULL,
  `Pieces` varchar(251) NOT NULL,
  `Phonenumber` varchar(251) NOT NULL,
  `Address` varchar(251) NOT NULL,
  `City` varchar(251) NOT NULL,
  `StateandProvince` varchar(251) NOT NULL,
  `Discription` varchar(251) NOT NULL
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci;

--
-- Dumping data for table `deliveryform`
--

INSERT INTO `deliveryform` (`Customername`, `Numberofcarton`, `Pieces`, `Phonenumber`, `Address`, `City`, `StateandProvince`, `Discription`) VALUES
('rwerwqe', 'qwrer', 'qwerewr', 'wqerqwe', 'qwerqw', 'qwerweq', 'weqrqwe', 'wqerwqer'),
('ahron', '12', '123', '094392', 'cogtong', 'candijay', 'bohol', 'eghsdjse'),
('jed', '21', '321', '094839892', 'candiay', 'tawid', 'cogtong', 'lagay'),
('jed edu', '12', '121', '2121', 'cogtong', 'candijay', 'bohol', 'ouhyeahhh'),
('jedkwaho', '21', '345', '095664322', 'candijay', 'cogtong', 'purok7', 'ouh yeahh'),
('ahronbayot', '12', '150', '0943421', 'thailand', 'bangkook', 'cogtong', 'na bayot nko'),
('12', '12', '12', '121', '12', '12', '12', '12'),
('ahron', '12', '12', '1234', '123456', '123', '123', '123'),
('', '', '', '', '', '', '', '123'),
('ahron', 'badili', '12', '12', 'COGTONG', 'CANDIJAY', 'BOHOL', 'thanks');

-- --------------------------------------------------------

--
-- Table structure for table `rideform`
--

CREATE TABLE `rideform` (
  `Firstname` varchar(251) NOT NULL,
  `Lastname` varchar(251) NOT NULL,
  `Addresslocation` varchar(251) NOT NULL,
  `Phonenumber` varchar(251) NOT NULL,
  `Emailaddress` varchar(251) NOT NULL,
  `Whatsapp` varchar(251) NOT NULL
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci;

--
-- Dumping data for table `rideform`
--

INSERT INTO `rideform` (`Firstname`, `Lastname`, `Addresslocation`, `Phonenumber`, `Emailaddress`, `Whatsapp`) VALUES
('ihrtjhnjbmq', 'ujurgufjh', 'Addressghugjfijokxd', 'hgtghfgh', 'thwrthfgh', 'trhtrhfg'),
('qwewqew', 'wqerwrqw', 'Addressqwerwqerwe', 'qwerweqr', 'wqrewrqw', 'qwerwqereqw'),
('121221', '212222', 'Address11222', '122121', '1111', '22222'),
('11111', '222222', 'Add33333333', '333333', '444434', '555555'),
('ahron', 'badili', 'cogtong', '09999999', '3434343', '434343434'),
('', '', '', '', '', ''),
('', '', '', '', '', ''),
('12', '12', '12', '12', '12', '12');

-- --------------------------------------------------------

--
-- Table structure for table `users`
--

CREATE TABLE `users` (
  `usersid` int(11) NOT NULL,
  `fname` varchar(250) NOT NULL,
  `lname` varchar(250) NOT NULL,
  `password` varchar(250) NOT NULL
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci;

--
-- Dumping data for table `users`
--

INSERT INTO `users` (`usersid`, `fname`, `lname`, `password`) VALUES
(1, 'jeremuah', 'patulombon', '12345'),
(2, 'chan', 'olonan', '12345'),
(3, 'kinsun', 'busis', '12345'),
(4, 'alvin', 'daganato', '12345');

--
-- Indexes for dumped tables
--

--
-- Indexes for table `accountdetails`
--
ALTER TABLE `accountdetails`
  ADD PRIMARY KEY (`accUsername`);

--
-- Indexes for table `users`
--
ALTER TABLE `users`
  ADD PRIMARY KEY (`usersid`);

--
-- AUTO_INCREMENT for dumped tables
--

--
-- AUTO_INCREMENT for table `users`
--
ALTER TABLE `users`
  MODIFY `usersid` int(11) NOT NULL AUTO_INCREMENT, AUTO_INCREMENT=5;
COMMIT;

/*!40101 SET CHARACTER_SET_CLIENT=@OLD_CHARACTER_SET_CLIENT */;
/*!40101 SET CHARACTER_SET_RESULTS=@OLD_CHARACTER_SET_RESULTS */;
/*!40101 SET COLLATION_CONNECTION=@OLD_COLLATION_CONNECTION */;
