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

The GPS listener module developed as follows:
 LocationManager (android module) -> fusedLocationProvider API (Google play services) -> fusedLocationProvider Client API (Google play services)
 
Latest module such as fusedLocationProvider Client API is of course far more easy to use and well abstracted.

<Review for each modules>
  1. LocationManager
   For LocationManager, We have to consider the Location Provider first. Wheteher it could be GPS, Network or Passive.
  Difference between Network and Passive is whether you use AGPS data or not.
  check Details about each provider : https://developerlife.com/2010/10/20/gps/#passive--cellid-wifi-macid
  -> First, choose which Provider will be used (availability can be checked for 'best provider')
  -> Second, we can choose the 'criteria' of getting location data - accuracy, battery-usage, cellular-data-used etc.
  -> Third, Set up locationListener interface for the data received
            We can use callbacks and other functions for data process
  
  It is not difficult to use, but still complex than later two because we have to set up more modules for use.
  
  2. fusedLocationProvider API
   To use this API, there are some prerequisites - Google play service
   -> First, check if google play service is available (can be either statically or dynamically)
   -> Second, set up callbacks and other request settings before reuquest.
   
   Normally, Last known location is used first for better performance. 
   Because there is possibility of the user starting the application not far from the previous location or else.
   
   The main advantage of using this API : 
   1) "battery-usage-optimization", 
   2) "fused" GPS & Network way of receiving location data
   These features are well abstracted by Google developers so No need to manually setup all the cases
   for example, use Network provider if GPS (Satellite) provider is unavailable in indoor, and as soon as Satellites
   are available, switch provider into GPS.
   
   3. fusedLocationProvider Client API
    Super easy to use because we do not even have to set up Google play service at first.
    Else is same as 2. fusedLocationProvider API
    
