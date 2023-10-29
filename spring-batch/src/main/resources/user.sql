-- 可以直接在相应的 MySQL 数据库里运行该脚本
CREATE TABLE IF NOT EXISTS `user` (
  `id` BIGINT NOT NULL PRIMARY KEY COMMENT '主键',
  `name` VARCHAR(255) DEFAULT NULL COMMENT '姓名',
  `age` INT DEFAULT NULL COMMENT '年龄'
) ENGINE = INNODB AUTO_INCREMENT = 6 DEFAULT CHARSET = utf8mb3;

INSERT INTO `user`(`id`, `name`, `age`)
VALUES
(1, 'dafei', 18),
(2, 'xiaofei', 17),
(3, 'zhongfei', 16),
(4, 'laofei', 15),
(5, 'feifei', 14);