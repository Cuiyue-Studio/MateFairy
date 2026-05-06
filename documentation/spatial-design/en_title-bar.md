The Title Bar displays the current page title, navigation controls (such as Back), key actions, and more. Typically, the information and actions in the Title Bar should be specific to the current page, but it may also include controls that are available globally, such as search and others.
![Image](https://p9-arcosite.byteimg.com/tos-cn-i-goo7wpa0wc/078f8ef59298415885ece6ef72d900ef~tplv-goo7wpa0wc-image.image)
## Anatomy
![Image](https://p9-arcosite.byteimg.com/tos-cn-i-goo7wpa0wc/476275ab39634ccf9e3b4f62083f6a11~tplv-goo7wpa0wc-image.image)

1. Container: The width matches the window.
2. Leading icon (optional): No restriction on image format; SVG is recommended. It is recommended to limit the quantity to no more than 2.
3. Title: Use words or phrases that succinctly describe the purpose of the window or view whenever possible.
4. Subtitle (optional): Provide supplementary explanation of the window information.
5. Tab bar (optional): This style is intended only for TitleBar, and Tab item content supports only text.
6. Grouping (optional): There are two grouping styles: spacing and divider line. It is not recommended to use both styles together.
7. End element (optional): It can be an independently interactive Icon Button or a Button.

## Alignment
The default alignment for titles is horizontally centered, but it can be changed to leading edge alignment of the window.
![Image](https://p9-arcosite.byteimg.com/tos-cn-i-goo7wpa0wc/6108f5120071494ba6ec12474ba38a36~tplv-goo7wpa0wc-image.image)

1. Center horizontally
2. Window leading edge alignment

## Interactive behavior
### Configuration menu
To avoid placing too many action buttons, less important or negative actions should be placed in the "More" menu first, where users can view all hidden actions.
![Image](https://p9-arcosite.byteimg.com/tos-cn-i-goo7wpa0wc/1388a8e9cb5741608869bbe1050167cb~tplv-goo7wpa0wc-image.image)
![Image](https://p9-arcosite.byteimg.com/tos-cn-i-goo7wpa0wc/fce6ce287fa74faaa1391d4186480051~tplv-goo7wpa0wc-image.image)
### Scroll
When the page is scrolled, the Title Bar is fixed at the top of the window by default. Developers can also customize elements to be hidden while scrolling and shown when scrolling stops.
![Image](https://p9-arcosite.byteimg.com/tos-cn-i-goo7wpa0wc/115ab158a51f48109251309b45edc086~tplv-goo7wpa0wc-image.image)
### Adaptive
The width of the Title Bar adapts to the window size, and during resizing, the Leading Icon and Trailing Element maintain their positional relationship with the window.
![Image](https://p9-arcosite.byteimg.com/tos-cn-i-goo7wpa0wc/2083593744e947ab88ce0c8143afa254~tplv-goo7wpa0wc-image.image)

