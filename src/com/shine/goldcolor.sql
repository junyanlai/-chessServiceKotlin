/*
Navicat MySQL Data Transfer

Source Server         : 默认连接
Source Server Version : 50719
Source Host           : localhost:3306
Source Database       : goldcolor

Target Server Type    : MYSQL
Target Server Version : 50719
File Encoding         : 65001

Date: 2018-05-23 19:41:37

*/

SET FOREIGN_KEY_CHECKS=0;

-- ----------------------------
-- Table structure for army
-- ----------------------------
DROP TABLE IF EXISTS `army`;
CREATE TABLE `army` (
  `id` int(11) NOT NULL,
  `aid` int(11) DEFAULT NULL,
  `adminid` int(11) DEFAULT NULL,
  `level` int(11) DEFAULT NULL,
  `num` int(11) DEFAULT NULL,
  `status` int(11) DEFAULT NULL,
  `name` tinytext,
  `date` tinytext,
  `announcement` varchar(255) DEFAULT NULL,
  `store` int(11) DEFAULT NULL,
  `donate` int(11) DEFAULT NULL,
  `donatecoin` int(11) DEFAULT NULL,
  `donategem` int(11) DEFAULT NULL,
  `competcoin` int(11) DEFAULT NULL,
  `competgem` int(11) DEFAULT NULL,
  `grain` int(11) DEFAULT NULL,
  `experience` int(11) DEFAULT NULL,
  `members` int(11) DEFAULT NULL,
  `expcenter` int(11) DEFAULT NULL,
  `expstore` int(11) DEFAULT NULL,
  `expbattle` int(11) DEFAULT NULL,
  `expmill` int(11) DEFAULT NULL,
  `exptree` int(11) DEFAULT NULL,
  `avatars` int(11) DEFAULT NULL,
  PRIMARY KEY (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8 COMMENT='军团';

-- ----------------------------
-- Records of army
-- ----------------------------
INSERT INTO `army` VALUES ('7', '930888', '4918', '0', '1', '0', 'wiwiwiwiwiwiwi', '2018-04-02 09:25:32', 'wiwiwiwiwiwiwiwiwiwiwiwiwiwiwiwiwi', '0', '50000', '0', '0', '0', '0', '0', '0', '4918', '0', '0', '0', '0', '0', '18');
INSERT INTO `army` VALUES ('8', '592455', '4918', '0', '1', '0', '这是一个军团', '2018-04-08 09:11:37', '我是军团公告', '0', '50000', '0', '0', '0', '0', '0', '0', '4918', '0', '0', '0', '0', '0', '4');
INSERT INTO `army` VALUES ('9', '803388', '4918', '0', '1', '0', '这是一个军团', '2018-04-08 09:11:37', '我是军团公告', '0', '50000', '0', '0', '0', '0', '0', '0', '4918', '0', '0', '0', '0', '0', '0');
INSERT INTO `army` VALUES ('10', '806800', '4936', '0', '1', '1', '嘴角上翘', '2018-04-08 14:52:05', '军团称号', '0', '50000', '0', '0', '0', '0', '0', '0', '4936', '0', '0', '0', '0', '0', '4');
INSERT INTO `army` VALUES ('11', '225568', '4918', '0', '2', '0', '使用者条款关闭', '2018-04-09 13:25:47', '使用者条款关闭', '0', '50000', '440', '106', '0', '0', '0', '0', '4918', '0', '22', '300', '0', '0', '0');
INSERT INTO `army` VALUES ('12', '449637', '4918', '0', '1', '0', '使用者条款关闭', '2018-04-08 14:52:05', '使用者条款关闭', '0', '50000', '0', '0', '0', '0', '0', '0', '4918', '0', '0', '0', '0', '0', '4');
INSERT INTO `army` VALUES ('13', '737904', '4918', '0', '1', '0', '初始化军团界面信息', '2018-04-08 14:52:05', '初始化军团界面信息', '0', '50000', '0', '0', '0', '0', '0', '0', '4918', '0', '0', '0', '0', '0', '4');
INSERT INTO `army` VALUES ('14', '432869', '4918', '0', '1', '0', '的委托', '2018-04-08 14:52:05', '的委托', '0', '50000', '0', '0', '0', '0', '0', '0', '4918', '0', '0', '0', '0', '0', '1');
INSERT INTO `army` VALUES ('15', '588456', '4918', '0', '1', '0', '正式成员', '2018-04-08 14:52:05', '正式成员', '0', '50000', '0', '0', '0', '0', '0', '0', '4918', '0', '0', '0', '0', '0', '2');
INSERT INTO `army` VALUES ('16', '513524', '4918', '0', '1', '0', '用此方法', '2018-04-08 14:52:05', '用此方法', '0', '50000', '0', '0', '0', '0', '0', '0', '4918', '0', '0', '0', '0', '0', '0');
INSERT INTO `army` VALUES ('17', '367546', '4918', '0', '1', '0', '我是军团', '2018-04-09 09:29:17', '我是公告', '0', '50000', '0', '0', '0', '0', '0', '0', '4918', '0', '0', '0', '0', '0', '0');
INSERT INTO `army` VALUES ('18', '328551', '4918', '0', '1', '0', '我是军团', '2018-04-09 09:29:17', '我是公告', '0', '50000', '0', '0', '0', '0', '0', '0', '4918', '0', '0', '0', '0', '0', '0');
INSERT INTO `army` VALUES ('19', '653171', '4918', '0', '1', '0', '我是军团', '2018-04-09 10:56:42', '我是公告', '0', '50000', '0', '0', '0', '0', '0', '0', '4918', '0', '0', '0', '0', '0', '4');
INSERT INTO `army` VALUES ('20', '852188', '4918', '0', '1', '0', '我是军团', '2018-04-09 10:56:42', '我是公告', '0', '50000', '0', '0', '0', '0', '0', '0', '4918', '0', '0', '0', '0', '0', '1');
INSERT INTO `army` VALUES ('21', '741366', '4918', '0', '1', '0', '我是军团', '2018-04-09 10:56:42', '我是公告', '0', '50000', '0', '0', '0', '0', '0', '0', '4918', '0', '0', '0', '0', '0', '0');
INSERT INTO `army` VALUES ('22', '748406', '4918', '0', '1', '0', '我是军团', '2018-04-09 11:44:42', '这是一个军团公告i', '0', '50000', '0', '0', '0', '0', '0', '0', '4918', '0', '0', '0', '0', '0', '0');
INSERT INTO `army` VALUES ('23', '741484', '4918', '0', '1', '0', '我是军团', '2018-04-09 11:44:42', '军团加入成功', '0', '50000', '0', '0', '0', '0', '0', '0', '4918', '0', '0', '0', '0', '0', '0');
INSERT INTO `army` VALUES ('24', '232314', '4918', '0', '1', '0', '66666666', '2018-04-09 13:25:47', '789789789', '0', '50000', '0', '0', '0', '0', '0', '0', '4918', '0', '0', '0', '0', '0', '1');
INSERT INTO `army` VALUES ('25', '892828', '4918', '0', '1', '0', '我是军团', '2018-04-10 13:16:51', '军团称号贼牛', '0', '50000', '0', '0', '0', '0', '0', '0', '4918', '0', '0', '0', '0', '0', '0');
INSERT INTO `army` VALUES ('26', '708082', '4918', '0', '1', '0', '军团中心界面', '2018-04-10 13:16:51', '军团中心界面', '0', '50000', '0', '0', '0', '0', '0', '0', '4918', '0', '0', '0', '0', '0', '0');
INSERT INTO `army` VALUES ('27', '773202', '4918', '0', '1', '0', '军团创建成功', '2018-04-10 13:16:51', '军团创建成功', '0', '50000', '0', '0', '0', '0', '0', '0', '4918', '0', '0', '0', '0', '0', '15');
INSERT INTO `army` VALUES ('28', '770043', '4918', '0', '1', '0', 'ID', '2018-04-10 13:16:51', 'ID', '0', '50000', '0', '0', '0', '0', '0', '0', '4918', '0', '0', '0', '0', '0', '15');
INSERT INTO `army` VALUES ('29', '225568', '4918', '0', '1', '0', '使用者条款关闭', '2018-04-09 13:25:47', '使用者条款关闭', '0', '0', '80', '4', '0', '0', '0', '0', '4900', '0', '22', '0', '0', '0', '0');
INSERT INTO `army` VALUES ('30', '641123', '4939', '0', '1', '0', '金币', '2018-04-12 13:52:58', '金币金币金币', '0', '50000', '0', '0', '0', '0', '0', '0', '4939', '0', '0', '0', '210', '0', '4');
INSERT INTO `army` VALUES ('31', '225568', null, null, '0', '0', '', '', '', '0', '0', '0', '0', '0', '0', '0', '0', '4939', '0', '0', '0', '0', '0', '0');
INSERT INTO `army` VALUES ('32', '770043', null, null, '0', '0', '', '', '', '0', '0', '0', '0', '0', '0', '0', '0', '4939', '0', '0', '0', '0', '0', '0');
INSERT INTO `army` VALUES ('33', '225568', null, null, '0', '0', '', '', '', '0', '0', '0', '0', '0', '0', '0', '0', '4939', '0', '0', '0', '0', '0', '0');
INSERT INTO `army` VALUES ('34', '806800', null, null, '0', '2', '', '', '', '0', '0', '0', '0', '0', '0', '0', '0', '4939', '0', '0', '0', '0', '0', '0');
INSERT INTO `army` VALUES ('35', '770043', null, null, '0', '0', '', '', '', '0', '0', '0', '0', '0', '0', '0', '0', '4939', '0', '0', '0', '0', '0', '0');
INSERT INTO `army` VALUES ('36', '770043', null, null, '0', '0', '', '', '', '0', '0', '0', '0', '0', '0', '0', '0', '4939', '0', '0', '0', '0', '0', '0');
INSERT INTO `army` VALUES ('37', '770043', null, null, '0', '0', '', '', '', '0', '0', '0', '0', '0', '0', '0', '0', '4939', '0', '0', '0', '0', '0', '0');
INSERT INTO `army` VALUES ('38', '770043', null, null, '0', '0', '', '', '', '0', '0', '0', '0', '0', '0', '0', '0', '4939', '0', '0', '0', '0', '0', '0');
INSERT INTO `army` VALUES ('39', '770043', null, null, '0', '0', '', '', '', '0', '0', '0', '0', '0', '0', '0', '0', '4939', '0', '0', '0', '0', '0', '0');
INSERT INTO `army` VALUES ('40', '225568', null, null, '0', '0', '', '', '', '0', '0', '0', '0', '0', '0', '0', '0', '4939', '0', '0', '0', '0', '0', '0');
INSERT INTO `army` VALUES ('41', '806800', null, null, '0', '2', '', '', '', '0', '0', '0', '0', '0', '0', '0', '0', '4939', '0', '0', '0', '0', '0', '0');
INSERT INTO `army` VALUES ('42', '806800', null, null, '0', '2', '', '', '', '0', '0', '0', '0', '0', '0', '0', '0', '4918', '0', '0', '0', '0', '0', '0');

-- ----------------------------
-- Table structure for fmcc
-- ----------------------------
DROP TABLE IF EXISTS `fmcc`;
CREATE TABLE `fmcc` (
  `gid` int(11) NOT NULL DEFAULT '0',
  `type` varchar(20) NOT NULL DEFAULT '',
  `detail` varchar(100) DEFAULT '',
  `status` int(4) DEFAULT '0',
  `fashion` int(11) DEFAULT '0',
  `mulGem` int(11) DEFAULT '100',
  `outDate` date DEFAULT NULL,
  `createDate` date DEFAULT NULL,
  `createUser` int(11) NOT NULL DEFAULT '0',
  `mulCoin` int(11) DEFAULT '100',
  `mulExp` int(11) DEFAULT '100',
  PRIMARY KEY (`gid`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;

-- ----------------------------
-- Records of fmcc
-- ----------------------------
INSERT INTO `fmcc` VALUES ('300001', 'hair', '波蘭卷', '1', '0', '100', '2020-04-24', '2018-04-24', '4900', '100', '100');
INSERT INTO `fmcc` VALUES ('380001', 'wears', '蝴蝶結', '1', '0', '100', '2020-04-24', '2018-04-24', '4900', '100', '100');
INSERT INTO `fmcc` VALUES ('400001', 'gift', '瑪莎拉蒂', '1', '8000', '100', '2020-04-24', '2018-04-24', '4900', '100', '100');
INSERT INTO `fmcc` VALUES ('400002', 'gift', '蘭博基尼', '1', '7000', '100', '2020-04-24', '2018-04-24', '4900', '100', '100');
INSERT INTO `fmcc` VALUES ('400003', 'gift', '黑鑽石', '1', '500', '100', '2020-04-24', '2018-04-24', '4900', '100', '100');
INSERT INTO `fmcc` VALUES ('400004', 'gift', '戒指', '1', '100', '100', '2020-04-24', '2018-04-24', '4900', '100', '100');

-- ----------------------------
-- Table structure for friend
-- ----------------------------
DROP TABLE IF EXISTS `friend`;
CREATE TABLE `friend` (
  `id` int(11) NOT NULL,
  `uid` int(11) NOT NULL DEFAULT '0',
  `fid` int(11) DEFAULT NULL,
  `times` varchar(20) DEFAULT '',
  `status` int(11) DEFAULT '0',
  `aid` int(11) DEFAULT '0',
  PRIMARY KEY (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;

-- ----------------------------
-- Records of friend
-- ----------------------------
INSERT INTO `friend` VALUES ('1', '4900', '4902', '2017-11-06 15:44:13.', '1', '4900');
INSERT INTO `friend` VALUES ('2', '4900', '4930', '2017-11-07 10:52:53.', '1', '4930');
INSERT INTO `friend` VALUES ('3', '4900', '4933', '2017-11-06 14:25:58.', '0', '4933');
INSERT INTO `friend` VALUES ('4', '4869', '4900', '2017-11-06 14:26:10.', '1', '4869');
INSERT INTO `friend` VALUES ('21', '4918', '4918', '2018-01-03 17:26:04.', '0', '4918');
INSERT INTO `friend` VALUES ('24', '4918', '4939', '2018-01-06 11:42:27.', '1', '4939');
INSERT INTO `friend` VALUES ('26', '4918', '4936', '2018-01-06 17:35:02.', '1', '4936');
INSERT INTO `friend` VALUES ('27', '4918', '4937', '2018-01-06 17:49:30.', '1', '4918');
INSERT INTO `friend` VALUES ('28', '4900', '4918', '2018-03-19 19:27:41.', '0', '4918');

-- ----------------------------
-- Table structure for goods
-- ----------------------------
DROP TABLE IF EXISTS `goods`;
CREATE TABLE `goods` (
  `gid` int(11) NOT NULL DEFAULT '0',
  `name` varchar(20) NOT NULL DEFAULT '',
  `type` varchar(20) NOT NULL DEFAULT '',
  `num` int(11) DEFAULT '-1',
  `fashion` int(11) DEFAULT '0',
  `detail` varchar(100) DEFAULT '',
  `status` int(4) DEFAULT '0',
  `price` int(11) NOT NULL DEFAULT '0',
  `currency` varchar(20) NOT NULL DEFAULT '',
  `outDate` date DEFAULT NULL,
  `createDate` date DEFAULT NULL,
  `createUser` int(11) NOT NULL DEFAULT '0',
  PRIMARY KEY (`gid`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;

-- ----------------------------
-- Records of goods
-- ----------------------------
INSERT INTO `goods` VALUES ('300001', '波蘭銀', 'hair', '-1', '0', '波蘭銀', '1', '20000', 'coin', '2020-04-24', '2018-04-24', '4900');
INSERT INTO `goods` VALUES ('380001', '蝴蝶結', 'wears', '-1', '0', '蝴蝶結', '1', '20000', 'coin', '2020-04-24', '2018-04-24', '4900');
INSERT INTO `goods` VALUES ('400001', '瑪莎拉蒂', 'gift', '-1', '8000', '瑪莎拉蒂', '1', '1000000', 'gem', '2020-04-24', '2018-04-24', '4900');
INSERT INTO `goods` VALUES ('400002', '蘭博基尼', 'gift', '-1', '7000', '蘭博基尼', '1', '1000000', 'gem', '2020-04-24', '2018-04-24', '4900');
INSERT INTO `goods` VALUES ('400003', '黑鑽石', 'gift', '-1', '500', '黑鑽石', '1', '200000', 'gem', '2020-04-24', '2018-04-24', '4900');
INSERT INTO `goods` VALUES ('400004', '戒指', 'gift', '-1', '100', '戒指', '1', '200000', 'gem', '2020-04-24', '2018-04-24', '4900');

-- ----------------------------
-- Table structure for store
-- ----------------------------
DROP TABLE IF EXISTS `store`;
CREATE TABLE `store` (
  `uid` int(11) NOT NULL,
  `GID300001` int(11) DEFAULT '0',
  `GID380001` int(11) DEFAULT '0',
  `GID400001` int(11) DEFAULT '0',
  `GID400002` int(11) DEFAULT '0',
  `GID400003` int(11) DEFAULT '0',
  `GID400004` int(11) DEFAULT '0',
  PRIMARY KEY (`uid`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;

-- ----------------------------
-- Records of store
-- ----------------------------
INSERT INTO `store` VALUES ('4900', '0', '0', '0', '0', '0', '0');
INSERT INTO `store` VALUES ('23452', '0', '0', '0', '0', '0', '0');

-- ----------------------------
-- Table structure for storef
-- ----------------------------
DROP TABLE IF EXISTS `storef`;
CREATE TABLE `storef` (
  `uid` int(11) NOT NULL,
  PRIMARY KEY (`uid`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;

-- ----------------------------
-- Records of storef
-- ----------------------------

-- ----------------------------
-- Table structure for user
-- ----------------------------
DROP TABLE IF EXISTS `user`;
CREATE TABLE `user` (
  `uid` int(11) NOT NULL AUTO_INCREMENT,
  `cid` int(11) DEFAULT '-1',
  `avatar` varchar(4000) DEFAULT '',
  `hash` varchar(4000) DEFAULT '',
  `nick` varchar(64),
  `line` int(11) DEFAULT '0',
  `name` varchar(64) DEFAULT '',
  `email` varchar(100),
  `phone` varchar(20) DEFAULT '',
  `accountType` varchar(20) DEFAULT '',
  `accountGG` varchar(30) DEFAULT '',
  `accountYH` varchar(30) DEFAULT '',
  `accountFB` varchar(30) DEFAULT '',
  `device` varchar(50) DEFAULT '',
  `hardware` varchar(30) DEFAULT '',
  `age` int(4) DEFAULT '0',
  `sex` int(4) DEFAULT '0',
  `level` int(20) DEFAULT '0',
  `vipLevel` int(20) DEFAULT '0',
  `exp` int(20) DEFAULT '0',
  `expFashion` int(20) DEFAULT '0',
  `medal0` varchar(11) DEFAULT '',
  `medal1` varchar(11) DEFAULT '',
  `medal2` varchar(11) DEFAULT '',
  `coin` int(21) DEFAULT '100000',
  `gem` int(21) DEFAULT '0',
  `bank` int(11) DEFAULT '0',
  `aid` int(11) DEFAULT '0',
  `rid` int(11) DEFAULT '0',
  `registerTime` varchar(30) DEFAULT '',
  `loginTime` varchar(30) DEFAULT '',
  `birthday` varchar(11) DEFAULT '',
  `tittle` varchar(11) DEFAULT '',
  `signTimes` int(11) DEFAULT '0',
  `lastSignDate` varchar(11) DEFAULT '',
  PRIMARY KEY (`uid`),
  UNIQUE KEY `user_nick_uindex` (`nick`),
  UNIQUE KEY `user_email_uindex` (`email`)
) ENGINE=InnoDB AUTO_INCREMENT=4930 DEFAULT CHARSET=utf8;

-- ----------------------------
-- Records of user
-- ----------------------------
INSERT INTO `user` VALUES ('4900', '9', '9', '01234567890123456789012345678901', '400大刀', '0', '虹西', 'colinst@qq.com', '17051092664', 'google', 'colin', '', '', '', '', '0', '0', '0', '0', '0', '0', '', '', '', '1000', '100', '0', '0', '0', '', '', '', '', '0', '');
INSERT INTO `user` VALUES ('4901', '-1', '1', '202CB962AC59075B964B07152D234B70', 'GM_Colin_1', '0', '', 'colin1@shine.com', '', 'email', '', '', '', '', '', '0', '0', '0', '0', '0', '0', '', '', '', '100000', '0', '0', '0', '0', '', '', '', '', '0', '');
INSERT INTO `user` VALUES ('4902', '-1', '1', '202CB962AC59075B964B07152D234B70', 'GM_Colin_2', '0', '0', 'colin2@shine.com', '', 'email', '', '', '', '', '', '0', '0', '0', '0', '0', '0', '', '', '', '10000000', '0', '0', '0', '0', '', '', '', '', '0', '');
INSERT INTO `user` VALUES ('4903', '-1', '1', '202CB962AC59075B964B07152D234B70', 'GM_Colin_3', '0', '0', 'colin3@shine.com', '', 'email', '', '', '', '', '', '0', '0', '0', '0', '0', '0', '', '', '', '10000000', '0', '0', '0', '0', '', '', '', '', '0', null);
INSERT INTO `user` VALUES ('4904', '-1', '1', '202CB962AC59075B964B07152D234B70', 'GM_Colin_4', '0', '0', 'colin4@shine.com', '', 'email', '', '', '', '', '', '0', '0', '0', '0', '0', '0', '', '', '', '10000000', '0', '0', '0', '0', '', '', '', '', '0', '');
INSERT INTO `user` VALUES ('4911', '-1', '8', 'B41CB62EC6767F2E41F9DF7A2D161515', '不要说话', '0', '吕国帅', 'm18300703807@163.com', '', 'email', '', '', '', '', '', '0', '0', '0', '0', '0', '0', '', '', '', '100000', '0', '0', '0', '0', '2018-04-21 10:33:02', '', '', '', '0', '');
INSERT INTO `user` VALUES ('4912', '-1', '6', '', 'liuliu654伏芬芬', '0', '', 'liuliu654伏芬芬', '', 'fast', '', '', '', '7014F79D3642247D0C89FB9D04B187C6', 'i', '0', '0', '0', '0', '0', '0', '', '', '', '100000', '0', '0', '0', '0', '2018-04-21 16:46:26', '', '', '', '0', '');
INSERT INTO `user` VALUES ('4914', '-1', '6', 'hash', 'nick', '0', '', 'asdfasf', '', 'email', '', '', '', '', '', '0', '0', '0', '0', '0', '0', '', '', '', '100000', '0', '0', '0', '0', '2018-04-24 11:29:57', '', '', '', '0', '');
INSERT INTO `user` VALUES ('4915', '-1', '1', '202CB962AC59075B964B07152D234B70', 'GM_黄1', '0', '', 'yao1@shine.com', '', 'email', '', '', '', '', '', '0', '0', '0', '0', '0', '0', '', '', '', '100000', '0', '0', '0', '0', '', '', '', '', '0', '');
INSERT INTO `user` VALUES ('4916', '-1', '2', '202CB962AC59075B964B07152D234B70', 'GM_黄2', '0', '', 'yao2@shine.com', '', 'email', '', '', '', '', '', '0', '0', '0', '0', '0', '0', '', '', '', '100000', '0', '0', '0', '0', '', '', '', '', '0', '');
INSERT INTO `user` VALUES ('4917', '-1', '3', '202CB962AC59075B964B07152D234B70', 'GM_黄3', '0', '', 'yao3@shine.com', '', 'email', '', '', '', '', '', '0', '0', '0', '0', '0', '0', '', '', '', '100000', '0', '0', '0', '0', '', '', '', '', '0', '');
INSERT INTO `user` VALUES ('4918', '-1', '4', '202CB962AC59075B964B07152D234B70', 'GM_黄4', '0', '', 'yao4@shine.com', '', 'email', '', '', '', '', '', '0', '0', '0', '0', '0', '0', '', '', '', '100000', '0', '0', '0', '0', '', '', '', '', '0', '');
INSERT INTO `user` VALUES ('4925', '-1', '1', '202CB962AC59075B964B07152D234B70', 'GM_吕1', '0', '', 'lv1@shine.com', '', 'email', '', '', '', '', '', '0', '0', '0', '0', '0', '0', '', '', '', '100000', '0', '0', '0', '0', '', '', '', '', '0', '');
INSERT INTO `user` VALUES ('4926', '-1', '2', '202CB962AC59075B964B07152D234B70', 'GM_吕2', '0', '', 'lv2@shine.com', '', 'email', '', '', '', '', '', '0', '0', '0', '0', '0', '0', '', '', '', '100000', '0', '0', '0', '0', '', '', '', '', '0', '');
INSERT INTO `user` VALUES ('4927', '-1', '3', '202CB962AC59075B964B07152D234B70', 'GM_吕3', '0', '', 'lv3@shine.com', '', 'email', '', '', '', '', '', '0', '0', '0', '0', '0', '0', '', '', '', '100000', '0', '0', '0', '0', '', '', '', '', '0', '');
INSERT INTO `user` VALUES ('4928', '-1', '4', '202CB962AC59075B964B07152D234B70', 'GM_吕4', '0', '', 'lv4@shine.com', '', 'email', '', '', '', '', '', '0', '0', '0', '0', '0', '0', '', '', '', '100000', '0', '0', '0', '0', '', '', '', '', '0', '');
INSERT INTO `user` VALUES ('4929', '-1', '6', '', '呂秀穎紅豆冰', '0', '', '呂秀穎紅豆冰', '', 'fast', '', '', '', '8D299289D916F80FB5BB002DE977E428', 'i', '0', '0', '0', '0', '0', '0', '', '', '', '100000', '0', '0', '0', '0', '2018-05-11 16:32:21', '', '', '', '0', '');
