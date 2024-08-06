# Python环境变量PYTHONPATH设置、导入第三方模块
暂时设置模块的搜索路径——修改sys.path
```text
import sys
sys.path.append('somepath')
```


永久设置模块的搜索路径——设置PYTHONPATH环境变量
anaconda下创建的虚拟环境，可以在对应的site-package文件夹下新建 .pth 文件，会自动加入sys.path下
e.g. ~/anaconda3/envs/{环境名}/lib/python3.7/site-packages 下新建 mine.pth，里面写入 /home/stone/projects/python，sys.path 下就会加入该路径

# Python在哪里查找模块
我们平时总用Python，最上面总是需要import一些模块，那么这些模块为什么呢够被引用到呢，Python是在哪里查找它们的呢？下面是我找资料总结的一些。

sys.path

Python在变量sys.path中查找模块，它是一个list，Python会在list内的路径下查找可用的模块

import sys

print(type(sys.path)) # <class 'list'>
for p in sys.path:
    print(p)

# 输出
# /Users/zjx/PycharmProjects/LearnPython/Other/Python在哪里查找模块
# /Users/zjx/PycharmProjects/LearnPython
# /Applications/PyCharm.app/Contents/plugins/python/helpers/pycharm_display
#
https://blog.csdn.net/zjxht62/article/details/119815407

# python常用命令—查看模块所在位置
环境：ipython3 交互式解释器

语法：

import 模块名

模块名.__file__

功能：

查看模块的所在位置

# typing常用类型
int、long、float: 整型、长整形、浮点型
bool、str: 布尔型、字符串类型
List、 Tuple、 Dict、 Set:列表、元组、字典、集合
Iterable、Iterator:可迭代类型、迭代器类型
Generator：生成器类型

1. 简介
typing模块为Python带来了类型提示和类型检查的能力。它允许开发者在代码中添加类型注解，提高代码的可读性和可维护性。尽管Python是一种动态类型语言，但类型注解能让开发者更清晰地了解函数和变量的预期类型。

2. 基本类型注解
a. 类型别名
typing模块中有多种内置的类型别名，比如List、Tuple、Dict等，可以用于注解变量和函数的预期类型。

from typing import List

def process_numbers(numbers: List[int]) -> int:
    return sum(numbers)
b. Union 类型
Union允许参数接受多种不同类型的数据。

from typing import Union

def double_or_square(number: Union[int, float]) -> Union[int, float]:
    if isinstance(number, int):
        return number * 2
    else:
        return number ** 2
c. Optional 类型
Optional表示参数可以是指定类型或者None。


from typing import Optional

def greet(name: Optional[str]) -> str:
    if name:
        return f"Hello, {name}!"
    else:
        return "Hello, World!"

https://www.jianshu.com/p/f9395e3c4fa3

# pydantic学习与使用 ------ 基本模型(BaseModel)使用
https://blog.csdn.net/IT_LanTian/article/details/123229717

from pydantic import BaseModel
 
class User(BaseModel):
    id: int
    name = 'yo yo'
    
类型name是从默认值(字符串)推断出来的，因此不需要类型注释（但是请注意当某些字段没有类型注释时有关字段顺序的警告）

user = User(id='123')

那么如何知道初始化的时候，需要哪些必填字段？可以通过 __fields_set__ 方法

print(user.__fields_set__)  # {'id'}

.dict() 可以将user对象的属性，转成字典格式输出，dict(user) 也是等价的

print(user.dict())  # {'id': 123, 'name': 'yo yo'}
print(dict(user))  # {'id': 123, 'name': 'yo yo'}
.json()可以将user对象的属性，转成json格式输出

print(user.json())  # {"id": 123, "name": "yo yo"}
BaseModel 模型属性
上面的例子只是展示了模型可以做什么的冰山一角。模型具有以下方法和属性：
dict()  返回模型字段和值的字典；参看。导出模型
json()  返回一个 JSON 字符串表示dict()；参看。导出模型
copy() 返回模型的副本（默认为浅拷贝）；参看。导出模型
parseobj() 如果对象不是字典，则用于将任何对象加载到具有错误处理的模型中的实用程序；参看。辅助函数
parseraw() 用于加载多种格式字符串的实用程序；参看。辅助函数
parsefile() 喜欢parseraw()但是对于文件路径；参看。辅助函数
fromorm() 将数据从任意类加载到模型中；参看。ORM模式
schema()  返回将模型表示为 JSON Schema 的字典；参看。图式
schemajson()  schema()返回;的 JSON 字符串表示形式 参看。图式
construct()  无需运行验证即可创建模型的类方法；参看。创建没有验证的模型
`__fields_set初始化模型实例时设置的字段名称集__fields模型字段的字典__config`  模型的配置类，cf。模型配置

from typing import List
from pydantic import BaseModel
 
class Foo(BaseModel):
    count: int
    size: float = None
 
class Bar(BaseModel):
    apple = 'x'
    banana = 'y'
 
class Spam(BaseModel):
    foo: Foo
    bars: List[Bar]
 
m = Spam(foo={'count': 4}, bars=[{'apple': 'x1'}, {'apple': 'x2'}])
print(m)
#> foo=Foo(count=4, size=None) bars=[Bar(apple='x1', banana='y'),
#> Bar(apple='x2', banana='y')]
print(m.dict())
"""
{
    'foo': {'count': 4, 'size': None},
    'bars': [
        {'apple': 'x1', 'banana': 'y'},
        {'apple': 'x2', 'banana': 'y'},
    ],
}
"""

# Python ABC：应用场景和示例
https://blog.csdn.net/wuShiJingZuo/article/details/136121120

# 彻底搞懂python super函数的作用
https://blog.csdn.net/wo198711203217/article/details/84097274

class Animal(object):
    def __init__(self, name):
        self.name = name

    def greet(self):
        print('Hello, I am %s.' % self.name)


class Dog(Animal):
    def greet(self):
        super(Dog, self).greet()  #调用父类Animal的greet方法
        print('WangWang...')


d=Dog("xiaohuang")

d.greet()


C:\python36\python.exe E:/demo/testPyQt.py
Hello, I am xiaohuang.
WangWang...

Process finished with exit code 0


class Base(object):
    def __init__(self, a, b):
        self.a = a
        self.b = b


class A(Base):
    def __init__(self, a, b, c):
        super(A, self).__init__(a, b)  # Python3 可使用 super().__init__(a, b)
        self.c = c

a=A(100,200,300)

print("a=%d, b=%d, c=%d" % (a.a,a.b,a.c))

