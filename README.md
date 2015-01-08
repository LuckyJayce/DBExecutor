DBExecutor
==========

 主要的功能
-----------------------------------

### 1.使用了读写锁，支持多线程操作数据。
### 2.支持事务
### 3.支持ORM
### 4.缓存Sql，缓存表结构
 
 
这个类库主要用于android 数据库操作。
始终围绕着一个类对应一个表的概念。
只要创建一个实体类，就不用当心它怎么存储在数据库中，不用重新写增删改查的代码。基本的功能已经帮你实现好了。
增删改查数据只要一句搞定
 
    boolean isSuccess = db.insert(person);
    boolean isSuccess = db.updateById(person);
    boolean isSuccess  = db.deleteById(Person.class,1);
    List<Person> persons = db.findAll(Person.class);
 
- ---library_DB02 是项目的源码。
- ---DBLibrary_TestCase 是项目的测试用例，主要介绍该类库详细的用法
- ---doc 是网页文档
- ---db.jar 是该类库的jar包
- ---doc.chm 是该类库的chm文档
- ---设计框架.docx 是该类库的uml图生成的图片
- ---设计框架.mdl 是该类库的uml图，描述整个类库的架构
 
如果有什么建议可以发我邮箱 告知。794629068@qq.com
 
最后非常感谢XUtils代码的作者wyouflf。
该代码是对开源项目XUtils的数据库模块进行重构，及性能优化。
因为他的框架设计的非常合理。很多都是在其基础上完善的。

## 1.定义一个简单对应表的实体类 ##
    @Table(name = "Person")
    public class Person {
     @Id(autoIncrement = true)
     private int id;
     private String name;
     private int age;
     
     public int getId() {
      return id;
     }
     
     public void setId(int id) {
      this.id = id;
     }
     
     public String getName() {
      return name;
     }
     
     public void setName(String name) {
      this.name = name;
     }
     
     public int getAge() {
      return age;
     }
     
     public void setAge(int age) {
      this.age = age;
     }
     
    }
必须包含@Id,@Id声明的字段对应表里的id。autoIncrement = true 自增长。
@Table 声明表的名字。@Table可以不需要。如果没有@Table以包名+类名为表的名字
 
支持基本类型以及基本类型的封装类，java.sql.Date 定义的字段。如果要支持其他类型的字段请查看DBLibrary_TestCase中的TestColumnConverter.java
 
## 2.主要类 ##
 
 
**1.DBExecutor主要用于执行sql语句，一个数据库对应一个DBExecutor**
 
  获取默认数据库的执行者
  DBExecutor executor = DBExecutor.getInstance(context);
  获取指定的数据库的执行者
  DBExecutor.getInstance(dbHelper)
 
  主要的方法用 db.execute(sqls)，db.executeQuery(sql)等
  封装了简基本的增删改查操作，使用db.insert(person);可以保存一条记录。
  执行sql的时候。不用考虑表是否创建。如果表不存在，会自动创建。
 
**2.Sql用于DBExecutor 执行的Sql**

  与sql文本语句是有差别的。 
  区别在于Sql 里包含    
  sql.getTable();//操作的表  
  sql.getSqlText();//可以带有?占位符的sql语句  
  sql.getBindValues();//?占位符对应的值。  

  sql.setCheckTableExit(checkTableExit);//设置执行sql时检查表是否存在，默认为true如果
  检查到表不存在自动创建，设置为false 不做检查
  不要害怕复杂，我们可以通过SqlFactory 创建它Sql
 
**3.SqlFactory主要用于创建Sql语句，可以创建复杂增删改查的sql语句。**
  
      // 查询语句
      Sql sql = SqlFactory.find(Person.class);
      sql = SqlFactory.find(Person.class).where("name", "=", "试着飞");
      sql = SqlFactory.find(Person.class).where("age", "=", "10").or("age", "=", 11);
      sql = SqlFactory.find(Person.class).where("age", "=", "10").orderBy("name", false);
      sql = SqlFactory.find(Person.class).where("age", "=", "10").orderBy("name", false).limit(1);
      sql = SqlFactory.find(Person.class).where("name", "like", "%飞");
      sql = SqlFactory.find(Person.class).where("name", "like", "_着_");
      sql = SqlFactory.find(Person.class).where("age", "in", new int[] { 10, 11, 12 });
      sql = SqlFactory.find(Person.class).where("name", "in", new String[] { "小明", "小红" });
      sql = SqlFactory.find(Person.class, "name", "age").where("name", "=", "试着飞");
      sql = SqlFactory.find(Person.class, "count(*) as num").where("age", "=", "11");
      sql = SqlFactory.find(Person.class, "max(age) as maxAge", "min(age) as minAge").where("sex", "=", "男");
      sql = SqlFactory.find(Person.class, new MaxFunction("age", "maxAge"), new MinFunction("age", "minAge")).where("sex", "=", "男");
      sql = SqlFactory.find(Person.class).where("name=? and age=?", new Object[] { "小明", 11 });
     
      // 删除语句
      sql = SqlFactory.deleteAll(Person.class);
      sql = SqlFactory.delete(Person.class).where("age", "=", 11);
      sql = SqlFactory.deleteById(Person.class, 11);
      sql = SqlFactory.deleteById(Person.class, new int[] { 11, 12 });
     
      // 更新语句
      sql = SqlFactory.update(Person.class, new String[] { "name", "age" }, new Object[] { "小明", "11" }).where("id", "=", 1);
      sql = SqlFactory.updateById(new Person(1, "小明", 11, "男"));
      sql = SqlFactory.updateById(new Person(1, "小明", 11, "男"), "name");
     
      // 如果存在 id为1的记录，就更新，否则 插入一条新记录
      sql = SqlFactory.updateOrInsertById(new Person(1, "小明", 11, "男"));
     
      // 插入语句
      sql = SqlFactory.insert(new Person(1, "小明", 11, "男"));
     
      // 自拼接sql语句
      sql = SqlFactory.makeSql(Person.class, "select * from Person where age = ?", new Object[] { 11 });
  
  **混淆配置**  
       -keepclasseswithmembers class * { @com.shizhefei.db.annotations.Id \<fields\>;  *;}  
  更多详细信息请查看项目文档里面的内容


License
=======

    Copyright 2014 shizhefei（LuckyJayce）

    Licensed under the Apache License, Version 2.0 (the "License");
    you may not use this file except in compliance with the License.
    You may obtain a copy of the License at

       http://www.apache.org/licenses/LICENSE-2.0

    Unless required by applicable law or agreed to in writing, software
    distributed under the License is distributed on an "AS IS" BASIS,
    WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
    See the License for the specific language governing permissions and
    limitations under the License.
