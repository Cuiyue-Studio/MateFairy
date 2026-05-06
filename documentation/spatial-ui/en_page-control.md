This article introduces the features and usage of the PageControl and ProgressPageControl components.
## PageControl
PageControl is a control designed according to the PICO design guidelines that displays a series of horizontal dots to indicate page progress. You can control progress by setting the total number of pages and the current index value. You can also limit the maximum number of display points; if this limit is exceeded, the guidance will gradually shrink.
![Image](https://p9-arcosite.byteimg.com/tos-cn-i-goo7wpa0wc/cd884e472ca44ba7a9cac4c32d0adc15~tplv-goo7wpa0wc-image.image)

### API Surface

* `currentIndex`: Currently selected index value.
* `onClickAction`: This callback is triggered when clicking changes the current index.
* `totalDots`: Represents the total number of pages. This value must be greater than or equal to 0.
* `selectIcon`: An optional `@Composable` callback function for customizing the icon shown when selected, which typically places an `Icon`.
* `colors`: Set the highlight (selected) and normal colors of the midpoint in the control. By default, `PageControlDefaults` sets the default color, and you can also customize the color through a `PageControlDefaults` instance.
* `enabled`: Specifies whether the control can be interacted with.
* `maxDisplayCount`: Sets the maximum number of points the control can display. Currently, the default maximum is 9. If there are more points than the maximum, some points will not be displayed on both the front end and back end, and the visible dots will gradually decrease in size.
* `pageControlSpec`: Set the control layout. You can set the radius of the dots, the spacing between dots, and the vertical spacing of the dots by customizing the `PageControlSpec` instance.

#### Basic usage
It is simply used as a pagination indicator. You can set the total number of dots and the current index, and switch to a different index by clicking.
```Kotlin
@Composable
fun TestPageControlChangeIndex() {
    Column(modifier = Modifier.background(BackGroundColor)) {
        var currentIndex by remember { mutableStateOf(0) }
        PageControl(
            currentIndex = currentIndex,
            onClickAction = { index ->
                currentIndex = index
                if (currentIndex > MaxDots) {
                    currentIndex = 0
                }
            },
            totalDots = MaxDots,
            colors =
                PageControlDefaults.pageControlColors(
                    highLightColor = Color.White,
                    normalColor = Color.Black
                ),
            enabled = true,
            pageControlSpec =
                PageControlDefaults.pageControlSpec(
                    dotRadius = 20.dp,
                    dotSpace = 20.dp,
                    verticalPadding = 20.dp,
                )
        )
        Text(text = "currentIndex: $currentIndex")
    }
}
```

<div style="text-align: center"><img src="https://p9-arcosite.byteimg.com/tos-cn-i-goo7wpa0wc/50939c71a11142808cb4f5892af67090~tplv-goo7wpa0wc-image.image" width="1280px" /></div>

### Advanced usage

* Customize the display style of selected items
   ```Kotlin
   @Preview
   @Composable
   fun CustomPageControlSample() {
       var current by remember { mutableIntStateOf(0) }
       Column(horizontalAlignment = Alignment.CenterHorizontally) {
           Text("Index value: $current")
           PageControl(
               currentIndex = current,
               onClickAction = {
                   current = it
               },
               totalDots = 16,
               colors = PageControlDefaults.pageControlColors(),
               selectIcon = {
                   Icon(
                       painter = painterResource(id = R.drawable.ic_sample_love),
                       contentDescription = "love",
                       modifier = Modifier.size(14.dp),
                   )
               },
               enabled = true,
               maxDisplayCount = PageControlDefaults.NormalMax,
               pageControlSpec = PageControlDefaults.Spec
           )
       }
   }
   ```

   <div style="text-align: center"><img src="https://p9-arcosite.byteimg.com/tos-cn-i-goo7wpa0wc/eb815bdff5094ad782ea104e7985adad~tplv-goo7wpa0wc-image.image" width="1280px" />   </div>

* In common paginated browsing, PageControl can synchronize its indicator when switching pages, and clicking navigates to a different corresponding page.
   ```Kotlin
   @Composable
   fun HorizontalPagerWithScrollableContent() {
       val pagerState = rememberPagerState { 12 }
       val currentInTotal = "${pagerState.currentPage + 1}/${pagerState.pageCount}"
       Box(
           modifier = Modifier
               .fillMaxWidth()
               .height(300.dp),
           contentAlignment = Alignment.Center
       ) {
           // Horizontal Pager
           HorizontalPager(
               modifier = Modifier.fillMaxSize(),
               state = pagerState,
               contentPadding = PaddingValues(20.dp),
               pageSpacing = 10.dp
           ) {
               Box(
                   modifier = Modifier
                       .fillMaxSize()
                       .padding(4.dp)
                       .background(if (it % 2 == 0) Color.Black else Color.Yellow),
                   contentAlignment = Alignment.Center
               ) {
                   Text(
                       text = currentInTotal,
                       color = if (it % 2 != 0) Color.Black else Color.Yellow
                   )
               }
           }
           PageControl(
               Synchronous indexing
               currentIndex = pagerState.currentPage,
               onClickAction = { },
               modifier = Modifier
                   .offset(y = 60.dp)
                   .background(Color.LightGray),
               numberOfDots = pagerState.pageCount,
           )
       }
   }
   ```

   <div style="text-align: center"><img src="https://p9-arcosite.byteimg.com/tos-cn-i-goo7wpa0wc/29e04f2e02bd41a0aaf51ed2d6a230d4~tplv-goo7wpa0wc-image.image" width="1280px" />   </div>


## ProgressPageControl
ProgressPageControl is an extension of the PageControl component under the PICO design guidelines, giving each selected dot a progress value. You can control the updates and changes to the control by using the currently selected index and its progress value.
![Image](https://p9-arcosite.byteimg.com/tos-cn-i-goo7wpa0wc/76e31b38160340ce96537366333f7ed5~tplv-goo7wpa0wc-image.image)
![Image](https://p9-arcosite.byteimg.com/tos-cn-i-goo7wpa0wc/e8a6591ed7d54c379491c56d677c4ce4~tplv-goo7wpa0wc-image.image)
![Image](https://p9-arcosite.byteimg.com/tos-cn-i-goo7wpa0wc/b130e3d2e4e2456ead3929866181678f~tplv-goo7wpa0wc-image.image)
### API Surface

* `currentIndex`: Index value of the current selection.
* `onClickAction`: This callback is triggered when clicking changes the current index.
* `numberOfDots`: This callback is triggered when the selection state changes. For example, the user clicked Option.
* `currentProgress`: Sets the current progress of the control.
* `colors`: Specifies the highlighted (selected) and normal colors for the midpoint of the control. You can set the default color through `PageControlDefaults`, or customize the color using a `PageControlDefaults` instance.
* `enabled`: Specifies whether the control is interactive.
* `maxDisplayCount`: Sets the maximum number of points that the control can display. The default is 16. If there are more points than this, some points on the front end and back end will not be shown, and the displayed dots will gradually shrink in size.
* `pageControlSpec`: Set control layout. You can set the radius of the dots, the spacing between dots, and the vertical spacing of the dots by customizing a `PageControlSpec` instance.

### Basic usage
Applicable to scenarios where progress is automatically updated in a loop.
```Kotlin
@Composable
fun ProgressPageControlSample() {
    var count by remember { mutableStateOf(CurrentValue) }
    var currentIndex by remember { mutableStateOf(4) }
    var progress by remember { mutableFloatStateOf(0.0f) }

    LaunchedEffect(true) {
        while (true) {
            delay(1000L) // Execute once every second
            progress += 0.1f
            if (progress >= 1.0f) {
                //Synchronously update the index when progress changes
                currentIndex += 1
                if (currentIndex >= MaxDots) {
                    currentIndex = 0
                }
                progress = 0.0f
            }
        }
    }
    Column(modifier = Modifier.background(BackGroundColor)) {
        ProgressPageControl(
            currentIndex = currentIndex,
            onClickAction = {
                count = it
            },
            currentProgress = { progress },
            numberOfDots = MaxDots
        )
        Text(text = "current index:$currentIndex Progress: $progress}")
    }
}
```

<div style="text-align: center"><img src="https://p9-arcosite.byteimg.com/tos-cn-i-goo7wpa0wc/e9938573dfd54cf584b5235cd5e582d0~tplv-goo7wpa0wc-image.image" width="1280px" /></div>

### Advanced usage
In an automatic carousel paging scenario, pages can be turned automatically based on progress or manually by clicking, and the maximum number of indicator dots displayed can be defined.
```Kotlin
@Composable
fun AutoScrollPagerWithContent() {
    val pageTotal = 12
    var currentIndex by remember { mutableStateOf(4) }
    var progress by remember { mutableFloatStateOf(0.0f) }
    val pagerState = rememberPagerState { pageTotal }
    val currentInTotal = "${pagerState.currentPage + 1}/${pagerState.pageCount}"
    val coroutine = rememberCoroutineScope()
    var clickChange by remember { mutableStateOf(false)  }
    LaunchedEffect(true) {
        launch {
            // Turn the page
            pagerState.animateScrollToPage(currentIndex)
        }
        while (true) {
            delay(1000L) // Execute every second
            if (clickChange) continue
            progress += 0.1f
            if (progress >= 1.0f) {
                currentIndex += 1
                if (currentIndex >= pageTotal) {
                    currentIndex = 0
                }
                progress = 0.0f
                delay(250)
                launch {
                    // Turn the page
                    pagerState.animateScrollToPage(currentIndex)
                }
            }
        }
    }

    Box(
        modifier = Modifier
            .fillMaxWidth()
            .height(300.dp),
        contentAlignment = Alignment.Center
    ) {
        HorizontalPager(
            modifier = Modifier.fillMaxSize(),
            state = pagerState,
            contentPadding = PaddingValues(20.dp),
            pageSpacing = 10.dp
        ) {
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(4.dp)
                    .background(if (it % 2 == 0) Color.Black else Color.Yellow),
                contentAlignment = Alignment.Center
            ) {
                Text(
                    text = currentInTotal,
                    color = if (it % 2 != 0) Color.Black else Color.Yellow
                )
            }
        }
        ProgressPageControl(
             Current progress
            currentIndex = pagerState.currentPage,
            onClickAction = {
                // Update current page
                clickChange = true
                currentIndex = it
                progress = 0f
                coroutine.launch {
                    // Update and switch to the page corresponding to the clicked index
                    pagerState.animateScrollToPage(it)
                    clickChange = false
                }
            },
            Current progress
            currentProgress = {
                progress
            },
            modifier = Modifier
                .offset(y = 60.dp)
                .background(Color.LightGray),
             // Total number of points
            numberOfDots = pagerState.pageCount,
            Maximum number of displayed points
            maxDisplayCount = 7
        )
    }
}
```

<div style="text-align: center"><img src="https://p9-arcosite.byteimg.com/tos-cn-i-goo7wpa0wc/78b20b13d7014921959399bdab14280d~tplv-goo7wpa0wc-image.image" width="1280px" /></div>



