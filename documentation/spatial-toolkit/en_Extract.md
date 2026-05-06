Generate a Float data stream from a single channel of the **Color N** or **Vector N** data stream.
![Image](https://p9-arcosite.byteimg.com/tos-cn-i-goo7wpa0wc/b7b44bc29719427392c6b65fc0fbd522~tplv-goo7wpa0wc-image.image)
## Parameter description

* **In**: Input data stream.
* **Index**: Index.

## Node usage instructions
The **Extract** node receives input from the **In** port and always outputs a single value of type **Float**. The output value is the number at the position specified by **Index** in **In**. For example, if **In** is a **Vector 3** with the value `(10, 15, 20)` and **Index** is `1`, the output is `15`. The default value of **Index** is `0`.

