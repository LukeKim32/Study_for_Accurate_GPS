# Study_for_Accurate_GPS
This simple study (java code) is derived from my Team's app development project. For better GPS accuracy

  For the start, there are mainly 2 ways to obtain my current location : GPS and Network
(GPS data can also be referred in other ways like GNSS or else, but I will call it as 'GPS' for simple.)
GPS data are received from Satellaties available, and Network data can be received by Cellular or Wi-fi routers.

Normally, GPS data are more accurate than Network received data, 
but there are pros and cons like GPS is mainly-outdoor-available,
so we should choose wheter to use both or just one appropriately.

For my team project, we needed more accurate location data for use, 
so Network data was unappropriate for its data difference.

<br/>
<hr/>
## Simple overview of Location data receiver


The Location data receiver module developed as follows: <br/>
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
    
