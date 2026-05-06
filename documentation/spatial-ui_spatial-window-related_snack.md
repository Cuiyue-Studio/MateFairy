Snack is similar to Android Toast and can be used to display brief notification messages at the bottom of the WindowContainer. It usually appears and disappears automatically after a few seconds. In addition to displaying the main information, it also provides interactive slots that allow customization of interactive behavior.
![Image](https://p9-arcosite.byteimg.com/tos-cn-i-goo7wpa0wc/35fc544a67654cfaa11d5cc791821ff4~tplv-goo7wpa0wc-image.image)
The Snack background color changes to match the application theme.

## API Surface
Provide two types of API:

* Simple version, limited customization:
   * `message`: Information to be displayed, of type String.
   * `description`: Supplementary information description, String type, optional.
   * `leadingIcon`: A left icon of Composable function type; optional; usually used together with `Icon`.
   * `action`: Information display duration. The default is 3 seconds.
* Flexible custom edition with a high degree of customization:
   * `message`: Information to be displayed, a Composable UI type, typically used with `Text`.
   * `description`: Supplementary information description, Composable UI type, typically used with `Text`
   * `leadingIcon`: An icon on the left side, of Composable UI type, optional, typically used with `Icon`.
   * `trailingActions`: This is an optional right-side area of the Composable UI type that can contain one or more components, typically IconButton, Button, and so on.
   * `duration`: Information display duration. The default is 3 seconds.

## Basic usage

1. `SnackHost`:
   * A component carries LocalSnackHostState and provides it to child View nodes through the LocalComposition mechanism. Typically, LocalSnackHostState only needs to be added at the root node of WindowContainer.
   * PicoTheme includes SnackHost by default. If WindowContainer uses PicoTheme, you do not need to configure SnackHost separately.
2. LocalSnackHostState: Can be accessed from any child node of SnackHost or PicoTheme.
3. Information is displayed and is hidden by default after 3 seconds.

```Kotlin
@Composable
fun SimpleSnack() {
    // 1. LocalSnackHostState can be set at any parent View node of the Button
    SnackHost {
        // 2. Any child node of SnackHost can access LocalSnackHostState
        val snackState = LocalSnackHostState.current
        val scope = rememberCoroutineScope()
        Button(onClick = { 
            scope.launch { 
                3. Display information
                snackState.show(message = "This is a Snack") 
            }
        }) {
            Text("SimpleSnack")
        }
    }
}
```

<div style="text-align: center"><img src="https://p9-arcosite.byteimg.com/tos-cn-i-goo7wpa0wc/9f573e7cf3db4423b5ce8d947112bbfb~tplv-goo7wpa0wc-image.image" width="1280px" /></div>

## Advanced usage

* Display icon and supporting description text
   ```Kotlin
   @Composable
   fun SnackWithLeftIcon() {
       // 1. Host LocalSnackHostState; you can set it at any parent View node of the Button.
       SnackHost {
           // 2. Any child node of SnackHost can access LocalSnackHostState
           val snackState = LocalSnackHostState.current
           val scope = rememberCoroutineScope()
           Button(
               onClick = {
                   scope.launch {
                       snackState.show(
                           message = "SnackWithLeftIcon", // Information
                           description = "Snack demo",  // Description
                           leadingIcon = {
                               Icon(painter = painterResource(R.drawable.ic_sample_listitem_leading),contentDescription = null)
                           }
                       )
                   }
               }
           ) {
               Text("SnackWithLeftIcon")
           }
       }
   }
   ```

   <div style="text-align: center"><img src="https://p9-arcosite.byteimg.com/tos-cn-i-goo7wpa0wc/c40833f10f11420f84be86c480a16afa~tplv-goo7wpa0wc-image.image" width="1280px" />   </div>

* Supports interaction and allows customization of business logic based on interaction results.
   * Examples of simple interactions are as follows:
      ```Kotlin
      val snackState = LocalSnackHostState.current
      val scope = rememberCoroutineScope()
      Button(
          onClick = {
              scope.launch {
                  val result = snackState.show(
                       message = "Leaving current site. Continue to external page?", 
                       action = "OK"
                  )
                  when(result) {
                      SnackResult.ActionPerformed -> {
                          // Actions after clicking the button
                      }
                      SnackResult.Dismissed -> {
                          // Snack closes automatically,
                      }       
                  }
              }
          }
      ) {
          Text("SnackWithAction")
      }
      ```

      <div style="text-align: center"><img src="https://p9-arcosite.byteimg.com/tos-cn-i-goo7wpa0wc/cb92151b963f416e897fe843ee1b346a~tplv-goo7wpa0wc-image.image" width="1280px" />      </div>

   * When you need to provide users with more complex interaction logic, please refer to the examples below:
      ```Kotlin
      val snackState = LocalSnackHostState.current
      val scope = rememberCoroutineScope()
      Button(
          onClick = {
              scope.launch {
                  snackState.show(
                      message = {
                          Text( "Important info" )
                      },
                      description = { Text("brief description") },
                      leadingIcon = {
                          CircularProgressIndicator(modifier = Modifier.size(13.dp))
                      },
                      trailingActions = {
                          Button 1
                          Button(onClick = { 
                              // Hide Snack after interaction; corresponds to SnackResult.ActionPerformed
                              this.performAction()
                          }, size = ButtonDefaults.Min) {
                              Text("OK")
                          }
                          // Button 2, close button
                          IconButton(onClick = { 
                              // Immediately hide Snack after interaction; corresponds to SnackResult.Dismissed
                              this.dismiss() 
                          }, size = IconButtonDefaults.Min) {
                              CloseIcon()
                          }
                      }
                  )
              }
          }
      ) {
          Text("SnackWithMultiAction")
      }
      ```

      <div style="text-align: center"><img src="https://p9-arcosite.byteimg.com/tos-cn-i-goo7wpa0wc/72ef962a9dc94c2eab6ab0b92ef1f4b4~tplv-goo7wpa0wc-image.image" width="1280px" />      </div>



