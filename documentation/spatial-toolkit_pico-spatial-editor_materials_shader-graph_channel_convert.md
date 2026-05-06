Convert the Input data stream from one data type to another.
![Image](https://p9-arcosite.byteimg.com/tos-cn-i-goo7wpa0wc/30c3577a0fa54192a44c3e0c3969ff7f~tplv-goo7wpa0wc-image.image)
### Parameter description

* **In**: The Input data stream to be converted.

### Node usage instructions
The **Convert** node processes data types according to the following rules:

* When converting **Float** to **Color** or **Vector**, the node copies the **Float** value to all channels of the **Color** or **Vector**.
* When converting **Color 3** to **Color 4**, the node sets the output Alpha channel to 1.0.
* When converting **Color 4** to **Color 3**, the node discards the Alpha channel.
* When converting **Bool** or **Integer** to **Float**, the output value is 1.0 or 0.0.
* When converting **Vector 2** to **Vector 3** or **Vector 3** to **Vector 4**, the node fills the new channel with 1.0.
* When converting **Vector 4** to **Vector 3** or **Vector 3** to **Vector 2**, the node discards the last channel.


