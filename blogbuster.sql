-- phpMyAdmin SQL Dump
-- version 5.0.2
-- https://www.phpmyadmin.net/
--
-- Servidor: localhost:3306
-- Tiempo de generación: 15-10-2021 a las 10:29:15
-- Versión del servidor: 8.0.21
-- Versión de PHP: 7.4.10

SET SQL_MODE = "NO_AUTO_VALUE_ON_ZERO";
START TRANSACTION;
SET time_zone = "+00:00";


/*!40101 SET @OLD_CHARACTER_SET_CLIENT=@@CHARACTER_SET_CLIENT */;
/*!40101 SET @OLD_CHARACTER_SET_RESULTS=@@CHARACTER_SET_RESULTS */;
/*!40101 SET @OLD_COLLATION_CONNECTION=@@COLLATION_CONNECTION */;
/*!40101 SET NAMES utf8mb4 */;

--
-- Base de datos: `blogbuster`
--

-- --------------------------------------------------------

--
-- Estructura de tabla para la tabla `post`
--

CREATE TABLE `post` (
  `id` int NOT NULL,
  `titulo` varchar(255) COLLATE utf8_unicode_ci NOT NULL,
  `cuerpo` text COLLATE utf8_unicode_ci,
  `fecha` datetime DEFAULT NULL,
  `etiquetas` varchar(255) COLLATE utf8_unicode_ci DEFAULT NULL,
  `visible` tinyint(1) NOT NULL
) ENGINE=InnoDB DEFAULT CHARSET=utf8 COLLATE=utf8_unicode_ci;

--
-- Volcado de datos para la tabla `post`
--

INSERT INTO `post` (`id`, `titulo`, `cuerpo`, `fecha`, `etiquetas`, `visible`) VALUES
(1, 'Neque porro quisquam est qui dolorem ipsum quia dolor sit amet, consectetur.', 'Lorem ipsum dolor sit amet, consectetur adipiscing elit. Duis tempus lacus in tellus tempor, vel pellentesque nisl tincidunt. Ut iaculis eleifend commodo. Mauris tortor arcu, tincidunt a aliquam vel, pulvinar eget ligula. Phasellus posuere rhoncus dolor. Sed dapibus est id aliquam pretium. Sed purus quam, sagittis non aliquet quis, congue vel ante. Mauris vitae feugiat sapien. Nulla quis libero felis. Vivamus eu auctor neque, sit amet aliquet purus. Proin gravida egestas lacus eu dignissim.\r\n\r\n', '2021-10-14 09:14:37', 'purus tortor pretium', 1),
(2, 'Quisquam est qui dolorem ipsum quia dolor sit amet, consectetur.', NULL, '2021-10-14 09:14:37', '', 1),
(3, 'Fusce lacus leo, suscipit vitae turpis vitae', 'Aliquam faucibus eros augue. Nullam ullamcorper volutpat nisl id pulvinar. Etiam vitae nisi vulputate, aliquam dolor vitae, tempor tellus. Sed bibendum justo nec ligula fermentum, id gravida neque sodales. Maecenas id libero et quam vehicula viverra. Donec viverra condimentum tellus vel mattis. Nunc mi enim, viverra vel nunc ut, pharetra condimentum felis. In et ligula facilisis, consectetur odio ac, porttitor sapien.', '2021-10-03 09:45:39', 'nullam ullamcorper volutpat', 1),
(4, 'Lacus leo, suscipit vitae turpis vitae', 'Aliquam faucibus eros augue. Nullam ullamcorper volutpat nisl id pulvinar. Etiam vitae nisi vulputate, aliquam dolor vitae, tempor tellus. Sed bibendum justo nec ligula fermentum, id gravida neque sodales. Maecenas id libero et quam vehicula viverra. Donec viverra condimentum tellus vel mattis. Nunc mi enim, viverra vel nunc ut, pharetra condimentum felis. In et ligula facilisis, consectetur odio ac, porttitor sapien.', '2021-10-04 09:45:39', 'nullam ullamcorper volutpat', 0),
(5, 'Leo, suscipit vitae turpis vitae', 'Aliquam faucibus eros augue. Nullam ullamcorper volutpat nisl id pulvinar. Vitae nisi vulputate, aliquam dolor vitae, tempor tellus. Sed bibendum justo nec ligula fermentum, id gravida neque sodales. Maecenas id libero et quam vehicula viverra. Donec viverra condimentum tellus vel mattis. Nunc mi enim, viverra vel nunc ut, pharetra condimentum felis. In et ligula facilisis, consectetur odio ac, porttitor sapien.', '2021-10-09 09:45:39', 'nullam ullamcorper volutpat', 1),
(12, 'hola', 'serrano', '0006-07-14 00:00:00', 'hola serrano que tal', 1);

--
-- Índices para tablas volcadas
--

--
-- Indices de la tabla `post`
--
ALTER TABLE `post`
  ADD PRIMARY KEY (`id`);

--
-- AUTO_INCREMENT de las tablas volcadas
--

--
-- AUTO_INCREMENT de la tabla `post`
--
ALTER TABLE `post`
  MODIFY `id` int NOT NULL AUTO_INCREMENT, AUTO_INCREMENT=13;
COMMIT;

/*!40101 SET CHARACTER_SET_CLIENT=@OLD_CHARACTER_SET_CLIENT */;
/*!40101 SET CHARACTER_SET_RESULTS=@OLD_CHARACTER_SET_RESULTS */;
/*!40101 SET COLLATION_CONNECTION=@OLD_COLLATION_CONNECTION */;
