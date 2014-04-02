
ENERGYLENS 

A system designed to infer human activities with respect to power consumption in residential settings 
where activity is defined "what appliance is switched ON?OFF , what is the duration of appliance usage,
what is the location of appliance and who is the occupant ?". 

Thus to achieve it, a combination of data from smart meter and data from sensors on smartphone such as
Wifi and Audio samples are fed as input ot the EnergyLens Algorithm which detects the activity with
the accuracy of precision = 78% and recall = 65% which is good compared to the meter only approach used
by NILM.

There are two separate streams of data, one from smart meter which are deployed one per apartment and
another stream of data comes from smartphone carried by the occupant while performing activities in 
the residece. The data collected from smartphone include Wifi scan of RSSI signal from APs in proximity
to the apartment. And second is the Audio samples collected from the microphone of the smartphone.

For collecting our second data stream we developed an android application which collects audio and wifi
samples in background without any user intervention every 20 seconds for a period of 10 second.This cycle
is repeated until the data collection is manually stopped by the user from the application. Audio 
samples are collected at the sample rate of 8KHz, the frame size used was 500ms (or 4000 samples) the 
reason for selecting the frame length being less power is consumed with higher frame length as compared
to frame of smaller length which samples very frequently and hence consumes more power.

For the sake of privacy, audio raw samples were not sent to server for processing by EnergyLens system 
rather the features were extracted from PCM samples on the phone itself and the raw samples are discarded,
the feature used is MFCC which also has been higly accounted for non-voice recognition.

			
