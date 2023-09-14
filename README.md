<p align="center" >
    <h3 align="center">data-factory</h3>
    <p align="center">
        根据SQL语句生成测试数据，简化开发、测试造数据的难度。
        <br>
        <a href="https://github.com/bombl/data-factory/"><strong>-- Home Page --</strong></a>
        <br>
        <br>
    </p>
</p>

## Introduction

根据输入的SQL语句（查询语句），生成测试数据；支持关联查询、简单子查询等。同时，支持数据预览、将生成的数据（INSERT语句）复制到剪切板或者直接保存到数据库。



## 功能介绍
- **数据工厂：** 指定SQL生成数据.
  ![](https://github.com/bombl/ImageHost/blob/main/datafactory1.png?raw=true)
  &nbsp;
- **配置修改：** 可以指定生成数据数量、修改数据生成条件（默认条件为SQL语句中的查询条件，支持手动修改或者增加新条件）.
  ![](https://github.com/bombl/ImageHost/blob/main/datafactory2.png?raw=true)
  &nbsp;
- **数据源管理：** 指定生成数据对应的数据源.
  ![](https://github.com/bombl/ImageHost/blob/main/datafactory3.png?raw=true)
  &nbsp;

## 特性
- 简单方便：基于SQL语句生成语句中所涉及表的数据，可自动生成多表关联数据；
- 数据预览：生成的数据支持预览；
- 持久化：支持将生成数据的INSERT语句复制到剪切板或者直接在数据库中执行；
- 多数据库类型：支持ORACLE、MySQL数据库。

## Contributing
Contributions are welcome! Open a pull request to fix a bug, or open an [Issue](https://github.com/bombl/data-factory/issues/) to discuss a new feature or change.

欢迎参与项目贡献！比如提交PR修复一个bug，或者新建 [Issue](https://github.com/bombl/data-factory/issues/) 讨论新特性或者变更。


## Copyright and License
This product is open source and free, and will continue to provide free community technical support. Individual or enterprise users are free to access and use.

- Licensed under the GNU General Public License (GPL) v3.
- Copyright (C) 2007 Free Software Foundation, Inc. <https://fsf.org/>.

产品开源免费，并且将持续提供免费的社区技术支持。个人或企业内部可自由的接入和使用。如有需要可邮件联系作者免费获取项目授权。