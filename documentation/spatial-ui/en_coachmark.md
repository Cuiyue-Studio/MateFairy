Coachmark is a component designed under the PICO design guidelines that displays content anchored to specific points. Coachmark consists of anchor points and content. CoachmarkBox provides the basic anchor point for display, and, together with SimpleCoachmark, RichCoachmark, ImageCoachmark, and others, is used to complete the final content display.
![Image](https://p9-arcosite.byteimg.com/tos-cn-i-goo7wpa0wc/101fc902f2904b3480986e9bd5ed7500~tplv-goo7wpa0wc-image.image)

* CoachmarkBox: It is the container for all Coachmarks and provides anchor points for child Coachmarks.
* SimpleCoachmark: A Coachmark for displaying simple information, such as text and optional buttons.
* RichCoachmark: A Coachmark for displaying multiple types of information, including images, titles, content, and buttons.
* ImageCoachmark: A Coachmark used to display image information and optional buttons.

## API Surface

* CoachmarkBox
   * `coachmark`: A Coachmark used for displaying content. Its subtypes typically include SimpleCoachmark, RichCoachmark, and ImageCoachmark.
   * `showCoachmark`: Determines whether to display the Coachmark. Boolean value, default is true.
   * `direction`: The direction of the Coachmark relative to the anchor view of the CoachmarkBox. The default direction for CoachmarkDirection is ToEnd, which means CoachmarkBox is displayed on the right side.
   * `gap`: The spacing between CoachmarkBox and Coachmark, which is provided by `CoachmarkDefaults.DefaultGap` by default.
   * `content`: The content currently displayed in CoachmarkBox.
* SimpleCoachmark
   * `text`: The current content of SimpleCoachmark is usually `Text`, but it can also be other custom content.
   * `button`: Optional button content in SimpleCoachmark, where custom content can be provided.
   * `backgroundColor`: Used to set the background color of SimpleCoachmark, and by default, the value is provided by the `CoachmarkDefaults.DefaultBackgroundColor` method.
   * `cornerSize`: Used to set the background corner radius of SimpleCoachmark.
* RichCoachmark
   * `image`: The current image of RichCoachmark.
   * `title`: The current title of RichCoachmark.
   * `buttons `: Optional button content in RichCoachmark; you can provide custom content.
   * `backgroundColor`: Used to set the background color of RichCoachmark. By default, the value is provided by the `CoachmarkDefaults.DefaultBackgroundColor` method.
   * `cornerSize`: Used to set the background corner radius of RichCoachmark.
   * `content`: Used to set the main content of RichCoachmark.
* ImageCoachmark
   * `image`: The current content of ImageCoachmark is usually `Image`.
   * `button`: Optional button content in ImageCoachmark. You can provide custom content.
   * `backgroundColor`: Used to set the background color of ImageCoachmark. The default value is provided by the `CoachmarkDefaults.DefaultBackgroundColor` method.
   * `cornerSize`: Used to set the background corner radius of SimpleCoachmark.
   * `padding`: The inner padding of ImageCoachmark. The default value is 8 dp.

## Basic usage
```Kotlin
@Composable
fun CurrentCoachmarkDemo() {
    // Controls whether the coachmark in the current CoachmarkBox is displayed
    var showCoachmark by remember { mutableStateOf(false) }
    CoachmarkBox(showCoachmark = showCoachmark, coachmark = {
        Display SimpleCoachmark
        SimpleCoachmark(
            text = { Text("Hello World") },
            button = {
                CoachmarkDefaults.CoachmarkButton(
                    onClick = { showCoachmark = false }
                ) {
                    Text("Action")
                }
            }
        )
    }) {
        IconButton(
            onClick = { showCoachmark = !showCoachmark },
            size = IconButtonDefaults.iconButtonSize(40.dp)
        ) {
            Icon(
                painter = painterResource(R.drawable.ic_sample_circle_placeholder),
                null
            )
        }
    }
}
```

<div style="text-align: center"><img src="https://p9-arcosite.byteimg.com/tos-cn-i-goo7wpa0wc/352e1beb215d4017bdc6f66e3f34b068~tplv-goo7wpa0wc-image.image" width="1280px" /></div>

## **Advanced usage**

* You can modify the CoachmarkBox direction to display anchor points in different orientations.
* RichCoachmark and ImageCoachmark can be used together to provide more Coachmark display options.

```Kotlin
@Composable
fun CurrentCoachmarkDemo() {
    // Controls whether the coachmark in the current CoachmarkBox is displayed
    var showCoachmark by remember { mutableStateOf(false) }
    // Set coachmark to display on the left
    CoachmarkBox(direction = CoachmarkDirection.ToStart , showCoachmark = showCoachmark, coachmark = {
        // Display RichCoachmark and configure image, title, buttons, content, and other content
        RichCoachmark(
            image =
                {
                    Image(
                        painter =
                            painterResource(
                                R.drawable.img_sample_header_of_modal_sheet
                            ),
                        contentDescription = null,
                        contentScale = ContentScale.Crop
                    )
                },
            title = { Text("Rich Coachmark Title") },
            content = {
                Text("Provide tips that will be useful to the reader navigating your app")
            },
            buttons = {
                CoachmarkDefaults.CoachmarkButton(
                    onClick = { showCoachmark = false }
                ) {
                    Text("Action")
                }
                CoachmarkDefaults.CoachmarkButton(
                    onClick = { showCoachmark = false }
                ) {
                    Text("Action")
                }
            }

        )
    }) {
        IconButton(
            onClick = { showCoachmark = !showCoachmark },
            size = IconButtonDefaults.iconButtonSize(40.dp)
        ) {
            Icon(
                painter = painterResource(R.drawable.ic_sample_circle_placeholder),
                null
            )
        }
    }
```

<div style="text-align: center"><img src="https://p9-arcosite.byteimg.com/tos-cn-i-goo7wpa0wc/58345a28b20e4a21b8a0e7b123c54b5b~tplv-goo7wpa0wc-image.image" width="1280px" /></div>

```Kotlin
@Composable
fun CurrentCoachmarkDemo() {
    // Controls whether the coachmark in the current CoachmarkBox is displayed
    var showCoachmark by remember { mutableStateOf(false) }
    // Set coachmark to display below
    CoachmarkBox(direction = CoachmarkDirection.Below , showCoachmark = showCoachmark, coachmark = {
        // ImageCoachmark, configure image
        ImageCoachmark(
            image = {
                Image(
                    painter =
                        painterResource(
                            R.drawable.img_sample_header_of_modal_sheet
                        ),
                    contentDescription = null,
                    contentScale = ContentScale.Crop
                )
            },
        )
    }) {
        IconButton(
            onClick = { showCoachmark = !showCoachmark },
            size = IconButtonDefaults.iconButtonSize(40.dp)
        ) {
            Icon(
                painter = painterResource(R.drawable.ic_sample_circle_placeholder),
                null
            )
        }
    }
}
```

<div style="text-align: center"><img src="https://p9-arcosite.byteimg.com/tos-cn-i-goo7wpa0wc/a5a56c872ef14c208bf23bf5504ae8c2~tplv-goo7wpa0wc-image.image" width="1280px" /></div>



