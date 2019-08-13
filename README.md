# Study_for_Accurate_GPS
This simple study (java code) is derived from my Team's app development project. For better GPS accuracy

*This document is not refined yet*
  
1m difference Location accuracy is introduced by Google android developers in 2018. <br>
check : https://www.youtube.com/watch?v=vywGgSrGODU <br> 
https://www.gpsworld.com/how-to-achieve-1-meter-accuracy-in-android/ <br>
 For summary, 
 > 1. *WiFi RTT : Far more accurate location in "indoor"* <br>
 > 2. *Dual Frequency : Far Far more accurate location also in "outdoor"*
 <br>
 
 1. WiFi RTT : <br>
 Location accuracy among indoor situation hasnt been better surprisingly for a while, but it's now.
 This is possible by using "Wifi RTT". It's simple as its name because normally network location is calculated by
 signal strength and its combination. But this has a problem of possibilities of far distance but same signal strength.
 But using RTT with wifi routers, we can achieve 1m difference location even in indoors. <br>
 
 *However, The formula is simple but has some constraints and these constraints are not overcome-able for now, near around.*
 <br>
 ### Not many WIFI rtt routers are capable for now
 <br> 
 > *Broadcom 802.11ac Acculocate Access Point* <br>
 > *Intel Dual Band Wireless-AC 8260* <br>
 > *Marvell AP-8964 802.11ac 4x4 Wave2 Concurrent Dual Band Access Point* <br>
 > *MediaTek MT663X 802.11abgn/ac Ref. STA* <br>
 > *Qualcomm IPQ4018 802.11ac 2-stream Dual-band, Dual-concurrent Router* <br>
 > *Qualcomm IPQ8065 802.11ac 4-stream Dual-band, Dual-concurrent Router* <br>
 > *Qualcomm Snapdragon 820 Development Kit* <br>
 > *Realtek RTL8812BU* <br>
 <br>
 It is known that only up those routers are available for wifi rtt. This H/W problem can be solved in future but not for now.
 So I think this method is not well applicable for our project application which targets ordinary people in use. <br>
 For more details check : https://www.netspotapp.com/what-is-wifi-rtt.html
 <br>
 
 2. Dual Frequency : <br>
 Outdoor GPS accurcay difference is normally known as 15~20m thesedays in clear sky. <br>
 We can measure our location data by getting encoded code from singal sent by Satellites. <br>
 Each of these signals are mono frequent and this is where the difference occurs. <br>
 But nowadays, some mobile phones containing Qualcomm Snapdragon late version can get dual-frequency from Satellites
 and we can get certainly accurate data. <br>
 ### However, this chips are not also common in mobile phones among most Korean users. 
 <br>
 *Not in Galaxy series from Korea because they use Samsungs's own chips (but in Japan and other countries, Snapdragon is used)* <br>
 check : https://developer.android.com/guide/topics/sensors/gnss.html
 L5 signal is the additional wave.
 
 So my team's project which requires accurate gps data is stalled for while because of these H/W constraints. <br> 
 I guess our idea is suitable further in future 
<br>

<hr/>
## Simple overview of Location data receiver
<br>
For the start, there are mainly 2 ways to obtain my current location : GPS and Network <br> 
(GPS data can also be referred in other ways like GNSS or else, but I will call it as 'GPS' for simple.) <br>
GPS data are received from Satellaties available, and Network data can be received by Cellular or Wi-fi routers. <br>

Normally, GPS data are more accurate than Network received data, <br>
but there are pros and cons like GPS is mainly-outdoor-available, <br>
so we should choose wheter to use both or just one appropriately. <br>

For my team project, we needed more accurate location data for use, <br> 
so Network data was unappropriate for its data difference. <br>
<br>
The Location data receiver module developed as follows: <br>
 LocationManager (android module) <br>
 -> fusedLocationProvider API (Google play services) <br>
 -> fusedLocationProvider Client API (Google play services)
<br>
Latest module such as fusedLocationProvider Client API is of course far more easy to use and well abstracted.

### <Review for each modules> 
  1. LocationManager <br>
   For LocationManager, We have to consider the Location Provider first. Wheteher it could be GPS, Network or Passive.<br>
  Difference between Network and Passive is whether you use AGPS data or not. <br>
  check Details about each provider : https://developerlife.com/2010/10/20/gps/#passive--cellid-wifi-macid <br>
  -> First, choose which Provider will be used (availability can be checked for 'best provider') <br>
  -> Second, we can choose the 'criteria' of getting location data - accuracy, battery-usage, cellular-data-used etc. <br>
  -> Third, Set up locationListener interface for the data received <br>
            We can use callbacks and other functions for data process <br>
  
  It is not difficult to use, but still complex than later two because we have to set up more modules for use. <br>
  
  2. fusedLocationProvider API <br>
   To use this API, there are some prerequisites - Google play service <br>
   -> First, check if google play service is available (can be either statically or dynamically)<br>
   -> Second, set up callbacks and other request settings before reuquest.<br>
   
   Normally, Last known location is used first for better performance. <br>
   Because there is possibility of the user starting the application not far from the previous location or else.<br>
   
   The main advantage of using this API : <br>
   1) "battery-usage-optimization", <br>
   2) "fused" GPS & Network way of receiving location data <br>
   These features are well abstracted by Google developers so No need to manually setup all the cases <br>
   for example, use Network provider if GPS (Satellite) provider is unavailable in indoor, and as soon as Satellites <br>
   are available, switch provider into GPS. <br>
   
   3. fusedLocationProvider Client API
    Super easy to use because we do not even have to set up Google play service at first. <br>
    Else is same as 2. fusedLocationProvider API <br>
    
