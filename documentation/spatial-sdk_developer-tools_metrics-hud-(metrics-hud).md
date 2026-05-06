You can use Metrics HUD to monitor device performance metrics in real time on the HMD.
## Environment requirements
The device system version must be 5.4.0 or above.
## Preparation
Enable "Developer" mode on the device.
## Enable Metrics HUD

1. Power on the VR all-in-one device.
2. Go to **Settings** > **Developer**.
3. Turn on the **Enable Metrics HUD** switch.

## Parameter configuration
### Basic settings
On the **SETTINGS** panel, configure parameters related to Metrics HUD display:
<div style="text-align: center"><img src="https://p9-arcosite.byteimg.com/tos-cn-i-goo7wpa0wc/afe1cdfbf0e34319a86e945c9d79e44e~tplv-goo7wpa0wc-image.image" width="532px" /></div>

| **Parameter** | **Description** |
| --- | --- |
| Quick-set Enabled Stats | NONE: Performance metric data is not displayed <br> BASIC: Basic performance metric data is displayed |
| Display Stats on Overlay | Select whether to display real-time statistics for the selected monitoring metrics (as shown in the red box below). <br> ![Image](https://p9-arcosite.byteimg.com/tos-cn-i-goo7wpa0wc/646936410c134878bcab2735a48e3304~tplv-goo7wpa0wc-image.image) |
| Display Graph on Overlay | Select whether to display real-time statistical graphs for the selected monitoring metrics (as shown in the red box below). <br> ![Image](https://p9-arcosite.byteimg.com/tos-cn-i-goo7wpa0wc/5235cfe286cf42de8f39b1a201aea485~tplv-goo7wpa0wc-image.image) |
| Scale | Set the scaling ratio of the HUD. |
| Distance | Set the distance between the HUD and the eyes. |
| Pitch | Set the rotation angle of the HUD along the X axis. |
| Yaw | Set the rotation angle of the HUD along the Y axis. |
### Select monitoring metrics
On the **STATS** panel, select the application performance metrics to monitor. For available metrics and descriptions, refer to the "Monitoring metrics description" section. You can configure the Enable and Graph options for each metric.
| **Option** | **Description** |
| --- | --- |
| Enable | When checked, the HUD will display real-time statistics for the corresponding metric. |
| Graph | When checked, the HUD will display real-time statistical graphs for the corresponding metric. |
## Monitoring metrics description
| **Metric name** | **Description** |
| --- | --- |
| GPU Utilization (GPU U) | Total GPU utilization (unit: %). |
| CPU Utilization (CPU U) | Total CPU utilization (unit: %). |
| FPS (FPS) | Frame rate (unit: frames per second). <br> ***Note***: Under normal conditions, the application's frame rate should be equal to the Display Refresh Rate. |
| Available Memory (A MEM) | Remaining available memory (unit: MB). |
| Performance Score (Perf S) <br>  | Current overall performance status of the device: <br>  <br> * <80: Performance is good <br> * [80,100]: Frame rate may drop in certain situations, such as prolonged operation, some heavy-load scenarios, frequency limiting in low battery scenarios, power outage, and more <br> * >100: Frequent frame rate drops |
| Foveation Level (FRL) | Foveated rendering level. The higher the level, the lower the GPU utilization, but the more noticeable the surrounding blur. Value range is -1 to 3: <br>  <br> * -1: Off <br> * 0：Low <br> * 1：Medium <br> * 2：High <br> * 3：TopHigh |
| Eye Buffer Width (EBW) | The width of the texture rendered by the engine. <br> Resolution has a direct impact on GPU rendering time. In the fragment shader, higher resolution requirements (that is, more pixels) result in longer rendering times. |
| Eye Buffer Height (EBH) | The height of the texture rendered by the engine. <br> Resolution has a direct impact on GPU rendering time. In the fragment shader, higher resolution requirements (that is, more pixels) result in longer rendering times. |
| Used Memory (U MEM) | Used memory (unit: MB). |
| Singlepass | Whether Multiview Rendering is enabled: <br>  <br> * 0: Not enabled <br> * 1: Enabled |
| CPU Temperature | Device CPU temperature (unit: °C). |
| GPU Temperature | Device GPU temperature (unit: °C). |
| CPU Level (CPU L) | CPU level set for the application. The higher the level, the greater the CPU usage. The default value is 0, indicating that the system automatically adjusts the application's CPU level. |
| GPU Level (GPU L) | GPU level set for the application. The higher the level, the greater the GPU usage. The default value is 0, indicating that the system automatically adjusts the application's GPU level. |
| Display Refresh Rate (DRR) | Device display refresh rate. |
| Battery Level (BAT) | Remaining battery power of the device (unit: %). |
| Battery Temperature (B TEM) | Device battery temperature (unit: °C). |
| Power Voltage (POW V) | Device voltage (unit: millivolts). |
| App VSS (VSS) | Virtual memory requested by the application from the system. |
| App PSS (PSS) | Physical memory actually used by the application. |
| App RSS (RSS) | Physical memory actually held by the application. |
| Battery Current ( BAT C) | Current from the battery (unit: mA). |
| GPU Frequency ( GPU F) | GPU clock speed. The higher the clock speed, the faster the GPU operates. |
| CPU Frequency (data for cores 0-7, including: CPU0 F, CPU1 F, CPU2 F, CPU3 F, CPU4 F, CPU5 F, CPU6 F, CPU7 F) | Clock speed of a single CPU core. The higher the clock speed, the faster the CPU operates. The application runs on cores 5, 6, and 7. <br>  |
| CPU Utilization (data for cores 0-7, including: GPU0 U, GPU1 U, GPU2 U, GPU3 U, GPU4 U, GPU5 U, GPU6 U, GPU7 U) | Utilization rate of a single CPU core. The application runs on cores 5, 6, and 7. <br>  |
| Current Now (CUR) | Real-time current. |
| Scene Average Current (SAC) | Average current in the scene. |
| GPU Frequency Proportion (GFP) | GPU frequency proportion in the current scene. |
| CPU User Average Load (CUAL) | Average CPU load of the foreground application process. |
## Is exporting raw performance data supported?
Metrics HUD only supports real-time viewing of application performance data on the HMD and does not support exporting raw data.
