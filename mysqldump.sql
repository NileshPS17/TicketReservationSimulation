-- MySQL dump 10.13  Distrib 5.7.16, for Linux (x86_64)
--
-- Host: localhost    Database: railway
-- ------------------------------------------------------
-- Server version	5.7.16-0ubuntu0.16.04.1

/*!40101 SET @OLD_CHARACTER_SET_CLIENT=@@CHARACTER_SET_CLIENT */;
/*!40101 SET @OLD_CHARACTER_SET_RESULTS=@@CHARACTER_SET_RESULTS */;
/*!40101 SET @OLD_COLLATION_CONNECTION=@@COLLATION_CONNECTION */;
/*!40101 SET NAMES utf8 */;
/*!40103 SET @OLD_TIME_ZONE=@@TIME_ZONE */;
/*!40103 SET TIME_ZONE='+00:00' */;
/*!40014 SET @OLD_UNIQUE_CHECKS=@@UNIQUE_CHECKS, UNIQUE_CHECKS=0 */;
/*!40014 SET @OLD_FOREIGN_KEY_CHECKS=@@FOREIGN_KEY_CHECKS, FOREIGN_KEY_CHECKS=0 */;
/*!40101 SET @OLD_SQL_MODE=@@SQL_MODE, SQL_MODE='NO_AUTO_VALUE_ON_ZERO' */;
/*!40111 SET @OLD_SQL_NOTES=@@SQL_NOTES, SQL_NOTES=0 */;

--
-- Table structure for table `reservation`
--

DROP TABLE IF EXISTS `reservation`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `reservation` (
  `reservation_id` int(11) NOT NULL AUTO_INCREMENT,
  `train_id` int(11) NOT NULL,
  `from_station` int(11) NOT NULL,
  `to_station` int(11) NOT NULL,
  `for_date` date NOT NULL,
  `no_of_seats` int(11) NOT NULL,
  `seat_type` enum('AC','CC','SL') NOT NULL,
  `total_amount` decimal(10,2) NOT NULL,
  PRIMARY KEY (`reservation_id`),
  KEY `fk_reservation_train_idx` (`train_id`),
  KEY `fk_reservation_from_idx` (`from_station`),
  KEY `fk_reservation_to_idx` (`to_station`),
  CONSTRAINT `fk_reservation_from` FOREIGN KEY (`from_station`) REFERENCES `stations` (`station_id`) ON DELETE CASCADE ON UPDATE CASCADE,
  CONSTRAINT `fk_reservation_to` FOREIGN KEY (`to_station`) REFERENCES `stations` (`station_id`) ON DELETE CASCADE ON UPDATE CASCADE,
  CONSTRAINT `fk_reservation_train` FOREIGN KEY (`train_id`) REFERENCES `trains` (`train_id`) ON DELETE CASCADE ON UPDATE CASCADE
) ENGINE=InnoDB AUTO_INCREMENT=123468 DEFAULT CHARSET=utf8;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `reservation`
--

LOCK TABLES `reservation` WRITE;
/*!40000 ALTER TABLE `reservation` DISABLE KEYS */;
/*!40000 ALTER TABLE `reservation` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `route_stops`
--

DROP TABLE IF EXISTS `route_stops`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `route_stops` (
  `train_id` int(11) NOT NULL,
  `station_id` int(11) NOT NULL,
  `stop_no` int(11) NOT NULL,
  `arr_time` time DEFAULT NULL,
  `dep_time` time DEFAULT NULL,
  `distance` decimal(10,0) NOT NULL,
  PRIMARY KEY (`train_id`,`station_id`),
  KEY `fk_stops_station_idx` (`station_id`),
  CONSTRAINT `fk_route_train` FOREIGN KEY (`train_id`) REFERENCES `trains` (`train_id`) ON DELETE NO ACTION ON UPDATE NO ACTION,
  CONSTRAINT `fk_stops_station` FOREIGN KEY (`station_id`) REFERENCES `stations` (`station_id`) ON DELETE CASCADE ON UPDATE CASCADE
) ENGINE=InnoDB DEFAULT CHARSET=utf8;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `route_stops`
--

LOCK TABLES `route_stops` WRITE;
/*!40000 ALTER TABLE `route_stops` DISABLE KEYS */;
INSERT INTO `route_stops` VALUES (12075,1,1,NULL,'14:25:00',0),(12075,2,2,'15:20:00','15:22:00',80),(12075,3,3,'18:00:00','18:03:00',150),(12075,4,4,'19:30:00','19:35:00',70),(12075,6,5,'21:50:00',NULL,110),(16358,1,1,NULL,'07:15:00',0),(16358,2,2,'08:00:00','08:02:00',80),(16358,3,4,'10:30:00','10:35:00',150),(16358,4,5,'11:00:00','11:02:00',70),(16358,5,3,'09:00:00','09:03:00',90),(16358,6,6,'12:30:00','12:40:00',110),(16358,7,8,'19:00:00',NULL,70),(16358,10,7,'14:30:00','14:33:00',220),(22021,3,1,NULL,'12:15:00',0),(22021,4,2,'14:30:00','14:33:00',70),(22021,8,3,'17:15:00','17:20:00',230),(22021,9,4,'22:00:00',NULL,180),(23100,2,1,NULL,'09:00:00',0),(23100,3,3,'11:45:00','11:48:00',150),(23100,5,2,'10:30:00','10:33:00',90),(23100,11,4,'13:30:00',NULL,80),(24831,3,1,NULL,'11:30:00',0),(24831,4,2,'13:00:00','13:02:00',70),(24831,6,3,'15:30:00',NULL,110);
/*!40000 ALTER TABLE `route_stops` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `seating_capacity`
--

DROP TABLE IF EXISTS `seating_capacity`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `seating_capacity` (
  `train_id` int(11) NOT NULL,
  `seat_type` enum('SL','CC','AC') NOT NULL,
  `no_of_seats` int(11) NOT NULL,
  `min_charge` decimal(10,0) NOT NULL,
  `rate` decimal(5,2) NOT NULL,
  PRIMARY KEY (`train_id`,`seat_type`),
  CONSTRAINT `fk_seating_capacity_1` FOREIGN KEY (`train_id`) REFERENCES `trains` (`train_id`) ON DELETE NO ACTION ON UPDATE NO ACTION
) ENGINE=InnoDB DEFAULT CHARSET=utf8;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `seating_capacity`
--

LOCK TABLES `seating_capacity` WRITE;
/*!40000 ALTER TABLE `seating_capacity` DISABLE KEYS */;
INSERT INTO `seating_capacity` VALUES (12075,'CC',10,30,0.91),(12075,'AC',7,150,1.82),(16358,'AC',20,150,2.73),(22021,'SL',10,150,0.91),(22021,'CC',10,50,0.91),(22021,'AC',7,250,2.73),(23100,'SL',15,150,0.91),(23100,'CC',15,50,0.91),(24831,'SL',10,100,0.91),(24831,'CC',10,10,0.91),(24831,'AC',7,250,2.73);
/*!40000 ALTER TABLE `seating_capacity` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `seats_reserved`
--

DROP TABLE IF EXISTS `seats_reserved`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `seats_reserved` (
  `reservation_id` int(11) NOT NULL,
  `person_id` varchar(45) NOT NULL,
  `name` varchar(45) NOT NULL,
  `age` int(11) NOT NULL,
  `seat_no` int(11) NOT NULL,
  PRIMARY KEY (`reservation_id`,`person_id`),
  CONSTRAINT `fk_seats_reserved_reservation_id` FOREIGN KEY (`reservation_id`) REFERENCES `reservation` (`reservation_id`) ON DELETE CASCADE ON UPDATE CASCADE
) ENGINE=InnoDB DEFAULT CHARSET=utf8;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `seats_reserved`
--

LOCK TABLES `seats_reserved` WRITE;
/*!40000 ALTER TABLE `seats_reserved` DISABLE KEYS */;
/*!40000 ALTER TABLE `seats_reserved` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `stations`
--

DROP TABLE IF EXISTS `stations`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `stations` (
  `station_id` int(11) NOT NULL,
  `name` varchar(45) NOT NULL,
  `code` varchar(45) NOT NULL,
  PRIMARY KEY (`station_id`),
  UNIQUE KEY `code_UNIQUE` (`code`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `stations`
--

LOCK TABLES `stations` WRITE;
/*!40000 ALTER TABLE `stations` DISABLE KEYS */;
INSERT INTO `stations` VALUES (1,'Thiruvananthapuram Central','TVC'),(2,'Kollam','QLN'),(3,'Eranakulam','ERS'),(4,'Thrissur','TCR'),(5,'Kottayam','KTYM'),(6,'Calicut','CLT'),(7,'Bangalore Cantt','BNC'),(8,'Coimbatore','CBE'),(9,'Madras Central','MAS'),(10,'Krishnarajapram','KJM'),(11,'Punalur','PUU');
/*!40000 ALTER TABLE `stations` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `trains`
--

DROP TABLE IF EXISTS `trains`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `trains` (
  `train_id` int(11) NOT NULL,
  `name` varchar(50) NOT NULL,
  `sun` tinyint(1) NOT NULL,
  `mon` tinyint(1) NOT NULL,
  `tue` tinyint(1) NOT NULL,
  `wed` tinyint(1) NOT NULL,
  `thu` tinyint(1) NOT NULL,
  `fri` tinyint(1) NOT NULL,
  `sat` tinyint(1) NOT NULL,
  PRIMARY KEY (`train_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `trains`
--

LOCK TABLES `trains` WRITE;
/*!40000 ALTER TABLE `trains` DISABLE KEYS */;
INSERT INTO `trains` VALUES (12075,'Jan Shatabdi',1,1,0,1,1,1,1),(16358,'Rajdhani Express',0,1,0,0,0,1,0),(22021,'Chennai Mail',1,0,0,1,0,0,0),(23100,'Kollam-Punalur Passenger',1,1,1,1,1,1,1),(24831,'Malabar Express',0,0,1,0,0,1,0);
/*!40000 ALTER TABLE `trains` ENABLE KEYS */;
UNLOCK TABLES;
/*!40103 SET TIME_ZONE=@OLD_TIME_ZONE */;

/*!40101 SET SQL_MODE=@OLD_SQL_MODE */;
/*!40014 SET FOREIGN_KEY_CHECKS=@OLD_FOREIGN_KEY_CHECKS */;
/*!40014 SET UNIQUE_CHECKS=@OLD_UNIQUE_CHECKS */;
/*!40101 SET CHARACTER_SET_CLIENT=@OLD_CHARACTER_SET_CLIENT */;
/*!40101 SET CHARACTER_SET_RESULTS=@OLD_CHARACTER_SET_RESULTS */;
/*!40101 SET COLLATION_CONNECTION=@OLD_COLLATION_CONNECTION */;
/*!40111 SET SQL_NOTES=@OLD_SQL_NOTES */;

-- Dump completed on 2016-11-22 14:16:34
