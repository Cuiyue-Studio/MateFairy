A tooltip is an informational text label that helps users understand the function of interface elements. The tooltip itself is not interactive. Typically used as an explanatory note for the parent element, such as a description of the Icon Button's functionality.
![Image](https://p9-arcosite.byteimg.com/tos-cn-i-goo7wpa0wc/2f264592afbe4ac18769a00279a40e69~tplv-goo7wpa0wc-image.image)
It is not recommended to display a Tooltip for buttons that already contain text, as the button text describes the function and repeating this information is unnecessary.
![Image](https://p9-arcosite.byteimg.com/tos-cn-i-goo7wpa0wc/4faa8d773d6d4405a88a3f1c7d510e10~tplv-goo7wpa0wc-image.image)
## Anatomy
![Image](https://p9-arcosite.byteimg.com/tos-cn-i-goo7wpa0wc/df0ce3bf8a34470bba99d950babf325e~tplv-goo7wpa0wc-image.image)

1. Container: The container size adapts to its content, with a maximum width of 240 dp.
2. Text: The copy should be as clear and concise as possible.
3. Subtext (optional): Supplementary explanatory copy.

## Interactive behavior
### Appearances and disappearances
Tooltip appears after hovering over the parent element for 1.5 seconds. Tooltip disappears immediately after leaving the interactive hotspot of the parent element. After a new Tooltip is triggered, the existing Tooltip disappears immediately. Only one Tooltip can be displayed at a time.

