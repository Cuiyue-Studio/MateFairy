TitleBar is a title bar component designed according to the PICO design guidelines, which arranges user-defined styles for the left, center, and right sections within a single row. It provides two modes for centering the title content: absolute centering and relative centering.
![Image](https://p9-arcosite.byteimg.com/tos-cn-i-goo7wpa0wc/850ce6cabbbb49758f13c3bcebbba094~tplv-goo7wpa0wc-image.image)

* **Absolute centering**
   ![Image](https://p9-arcosite.byteimg.com/tos-cn-i-goo7wpa0wc/4cf150ff5b134bffa96a0175cae79b11~tplv-goo7wpa0wc-image.image)
* **Center relatively**
   ![Image](https://p9-arcosite.byteimg.com/tos-cn-i-goo7wpa0wc/fc8f615a019a4b94bce5041a6fe9e9a0~tplv-goo7wpa0wc-image.image)

## API Surface

* `title`: Title slot: customizable content.
* `leadingActions`: Top content slot for customizing the content displayed at the top; optional.
* `trailingActions`: Slot for custom trailing content; optional.
* `leadingGap`: Reserved space at the top. Default is 8 dp. Size can be customized.
* `trailingGap`: Reserved space at the end. The default value is 8 dp, and the size can be customized.
* `titleAlignment`: Title alignment type, provides two options: absolute center and relative center. The default is relative center.
* `colors`: Color settings for the titleBar. You can use `TitleBarDefaults` to set the colors of `title`, `leadingActions`, and `trailingActions`.

## Basic usage
Commonly used for displaying navigation bars and adding event items to the header and footer.
```Kotlin
@Composable
fun TitleBarWithCenterTitleSample() {
    TitleBar(
        title = { Text("Title")},
        leadingActions = {
           IconButton (onClick = {}) {
               Icon(
                   painter = painterResource(id = R.drawable.ic_sample_search),
                   contentDescription = null
               )
           }
        },
        trailingActions = {
            IconButton(onClick = {}) {
              Icon(
                  painter = painterResource(id = R.drawable.sample_more),
                  contentDescription = null
              )
           }
        }
    )
}
```

<div style="text-align: center"><img src="https://p9-arcosite.byteimg.com/tos-cn-i-goo7wpa0wc/c768c39c0b69479087c5a8e5eeb36fc2~tplv-goo7wpa0wc-image.image" width="1280px" /></div>

## Advanced usage
Title alignment can be customized, multiple items can be placed in the title slot, and items can be scrolled for display. These features are commonly used in pagination control bars.

* **Title alignment**
   * Absolutely centered, with the title positioned at the center of the navigation bar.
      ```Kotlin
      @Composable
      fun TitleBarWithCenterTitleSample() {
          Box {
              TitleBar(
                  modifier = Modifier.background(PicoTheme.colorScheme.onAccent),
                  title = { Text("Title") },
                  leadingActions = { SimpleButton() },
                  trailingActions = {
                      SimpleButton()
                      SimpleButton()
                      SimpleButton()
                  },
                  titleAlignment = TitleAlignment.CenterInBar
              )
          }
      }
      ```

      <div style="text-align: center"><img src="https://p9-arcosite.byteimg.com/tos-cn-i-goo7wpa0wc/2e56be3db20e412ab72527b7502eea50~tplv-goo7wpa0wc-image.image" width="1280px" />      </div>

   * Relatively centered, with the title positioned midway between the beginning and end.
      ```Kotlin
      @Composable
      fun TitleBarStartTitleSample() {
          Box {
              TitleBar(
                  modifier = Modifier.background(PicoTheme.colorScheme.onAccent),
                  title = { Text("Title") },
                  leadingActions = { SimpleButton() },
                  trailingActions = {
                      SimpleButton()
                      SimpleButton()
                      SimpleButton()
                  },
                  titleAlignment = TitleAlignment.Center
              )
          }
      }
      ```

      <div style="text-align: center"><img src="https://p9-arcosite.byteimg.com/tos-cn-i-goo7wpa0wc/e3af220f40c84ac5a26cfa75b7f46cef~tplv-goo7wpa0wc-image.image" width="1280px" />      </div>

* **Usage in scenarios involving the pagination navigation bar**
   * Add items for multiple pages and display them by swiping.
      ```Kotlin
      @Composable
      fun TitleBarMultipleTitlesAndActionsSample() {
          TitleBar(
              modifier = Modifier.background(PicoTheme.colorScheme.onAccent),
              titleAlignment = TitleAlignment.Center,
              title = {
                  val scrollState = rememberScrollState()
                  Row(
                      modifier = Modifier
                          .horizontalScroll(scrollState)
                          .padding(horizontal = 24.dp),
                      horizontalArrangement = Arrangement.spacedBy(8.dp)
                  ) {
                      Item(text = "Item")
                      Item(text = "Item")
                      Item(text = "Item")
                      Item(text = "Item")
                      Item(text = "Item")
                      Item(text = "Item")
                      Item(text = "Item")
                  }
              },
              leadingActions = {
                  SimpleButton()
                  SimpleButton()
              },
              trailingActions = {
                  SimpleButton()
                  SimpleButton()
                  SimpleButton()
                  SimpleButton()
              }
          )
      }
      ```

      <div style="text-align: center"><img src="https://p9-arcosite.byteimg.com/tos-cn-i-goo7wpa0wc/5a1a54f439b24ef7a052b28316ac15ed~tplv-goo7wpa0wc-image.image" width="1280px" />      </div>

   * Add a side gradient mask to optimize the slide-out effect when there are multiple items
      ```Kotlin
      @Composable
      fun TitleBarMultipleTitlesAndActionsSample2() {
          TitleBar(
              modifier = Modifier.background(PicoTheme.colorScheme.onAccent),
              titleAlignment = TitleAlignment.Center,
              title = {
                  val scrollState = rememberScrollState()
                  Row(
                      modifier =
                      Modifier
                          .graphicsLayer(compositingStrategy = CompositingStrategy.Offscreen)
                          .drawWithContent {
                              drawContent()
                              // Draw gradient semi-transparent mask
                              drawRect(
                                  brush =
                                  Brush.linearGradient(
                                      listOf(Color.Black, Color.Transparent),
                                      end = Offset(size.width, 0f)
                                  ),
                                  topLeft = Offset(size.width - 30.dp.toPx(), 0f),
                                  size = Size(width = 30.dp.toPx(), height = size.height),
                                  blendMode = BlendMode.DstIn,
                              )
                          }
                          .horizontalScroll(scrollState)
                          .padding(horizontal = 24.dp),
                      horizontalArrangement = Arrangement.spacedBy(8.dp)
                  ) {
                      Item(text = "Item")
                      Item(text = "Item")
                      Item(text = "Item")
                      Item(text = "Item")
                      Item(text = "Item")
                      Item(text = "Item")
                      Item(text = "Item")
                  }
              },
              leadingActions = {
                  SimpleButton()
                  SimpleButton()
              },
              trailingActions = {
                  SimpleButton()
                  SimpleButton()
                  SimpleButton()
                  SimpleButton()
              }
          )
      }
      ```

      <div style="text-align: center"><img src="https://p9-arcosite.byteimg.com/tos-cn-i-goo7wpa0wc/99d933ef390f416db9c8c50974048b0e~tplv-goo7wpa0wc-image.image" width="1280px" />      </div>



