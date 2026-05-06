A blending operation that uses the foreground to darken the background layer. The calculation formula is: `1 - (1 - B) / F`.
![Image](https://p9-arcosite.byteimg.com/tos-cn-i-goo7wpa0wc/13ea1615da6d426998c576ba2f5c3743~tplv-goo7wpa0wc-image.image)
## Parameter description

* **Foreground**: Foreground Input. Denoted as `F` in the formula.
* **Background**: Background Input. Denoted as `B` in the formula.
* **Mix**: The weight of the blending effect. The higher the **Mix**  value, the stronger the blending operation and the more pronounced the visual effect. The default value is `1`. Values outside the range of `0` to `1` will result in undefined effects beyond the expected functionality of this node.

## Node usage instructions
The **Burn** node deepens each area of the background based on the darkness of the corresponding area in the foreground.
The following Shader Graph uses the **Noise 2D** node to generate Perlin noise, and connects the output texture to the **Burn**  node's **Foreground** Input. This darkens the background brick texture.
![Image](https://p9-arcosite.byteimg.com/tos-cn-i-goo7wpa0wc/946e9565c0c24b72a904472672373e4f~tplv-goo7wpa0wc-image.image)

<div style="display: flex;">
<div style="flex-shrink: 0;width: calc((100% - 16px) * 0.5000);">

<div style="text-align: center">Darkening effect when <strong>Mix</strong> = 0</div>

![Image](https://p9-arcosite.byteimg.com/tos-cn-i-goo7wpa0wc/e629bc26cd634bbd80ba50250b56875b~tplv-goo7wpa0wc-image.image)



</div>
<div style="flex-shrink: 0;width: calc((100% - 16px) * 0.5000);margin-left: 16px;">

<div style="text-align: center">Darkening effect when <strong>Mix</strong> = 0.3</div>

![Image](https://p9-arcosite.byteimg.com/tos-cn-i-goo7wpa0wc/5a5b16f219184f2fa0c47e9573e3b545~tplv-goo7wpa0wc-image.image)



</div>
</div>




