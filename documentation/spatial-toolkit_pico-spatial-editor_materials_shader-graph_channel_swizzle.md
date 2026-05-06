Perform arbitrary rearrangement of the channels of the Input stream and return a new data stream of a specified type.
![Image](https://p9-arcosite.byteimg.com/tos-cn-i-goo7wpa0wc/3dfb679d0d6e4605a4f14ed0b3af8f69~tplv-goo7wpa0wc-image.image)
### Parameter description

* **In**: Input data stream.
* **Channels**: Specify how the input channels are rearranged to form the output using a string.

### Node usage instructions
The output of the **Swizzle** node depends on the **Channels** parameter. Each character in the **Channels** string represents a channel of the **In** parameter. For example, if `In` is a **Vector 3 Vector 3** with a value of `(1, 5, 10)`, then "x" corresponds to `1`, "y" corresponds to `5`, and "z" corresponds to `10`. The order of the characters determines the anatomy of the input channels for the output. In the example above, if the **Channels** parameter is "zzz", the output is `(10, 10, 10)`.
The length of the **Channels** string must be equal to the number of output channels.

![Image](https://p9-arcosite.byteimg.com/tos-cn-i-goo7wpa0wc/b23e0685d9aa4527b70acde30e37229d~tplv-goo7wpa0wc-image.image)
The following table shows more examples of processing by the **Swizzle** node:
| **Type** | **In** | **Channels** | **Out** |
| --- | --- | --- | --- |
| Vector 3 Vector 3 | (1, 5, 10) | zzz | (10, 10, 10) |
| Vector 3 Vector 3 | (1, 5, 10) | zyx | (10, 5, 1) |
| Vector 2 Vector 3 | (5, 0) | xxy | (5, 5, 0) |
| Vector 3 Vector 2 | (1, 5, 10) | zx | (10, 1) |
| Vector 3 Color 3 | (0.5, 0.8, 0) | grb | (0.8, 0.5, 0) |
