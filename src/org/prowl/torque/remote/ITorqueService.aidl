package org.prowl.torque.remote;

	/*
		0x03 	Fuel system status
		0x04 	Calculated engine load value
		0x05 	Engine coolant temperature
		0x06 	Short term fuel trim (bank 1)
		0x07 	Long term fuel trim (bank 1)
		0x08 	Short term fuel trim (bank 2)
		0x09 	Long term fuel trim (bank 2)
		0x0a 	Fuel pressure
		0x0b 	Intake manifold absolute pressure (MAP)
		0x0c 	Engine RPM
		0x0d 	Vehicle speed
		0x0e 	Timing advance
		0x0f 	Intake air temperature (IAT)
		0x10 	Mass air flow rate (MAF)
		0x11 	Throttle position (at manifold), this is not the accelerator pedal, and this is a relative reading.
			(Rest of PIDs(above) are as per MODE $01 SAE spec), next follows Torque internal PIDs
		0xff1005 	GPS Longitude
		0xff1006 	GPS Latitude
		0xff1001 	GPS Speed
		0xff1010 	GPS Height
		0xff1007 	GPS Bearing
		0xff1201 	MPG
		0xff1202 	Turbo Boost
		0xff1203 	Kilometers Per Litre
		0xff1205 	Trip MPG
		0xff1206 	Trip KPL
		0xff1207 	Litres per Kilometre
		0xff1208 	Trip LPK
		0xff120A 	Manifold Vacuum
		0xff120B 	GPS Trip distance
		0xff120C 	Vehicle distance (Odometer) saved with profile
		0xff1220 	Accelerometer (X)
		0xff1221 	Accelerometer (Y)
		0xff1222( 	Accelerometer (Z)
		0xff1223 	Accelerometer (Total)
		0xff1225 	Torque
		0xff1226 	Horsepower
		0xff122d 	0-60 mph time
		0xff122e 	0-100 kph time
		0xff122f 	Quarter mile time
		0xff1230 	eighth mile time
		0xff1237 	GPS vs OBD speed diff
		0xff1238 	Voltage
		0xff1239 	GPS Accuracy
		0xff123A 	GPS Satellites locked
		0xff123B 	GPS bearing
		0xff1249 	AFR
		0xff1214->ff121B 	O2 Sensors 1x1->1x4, 2x1->2x4
		0xff1240->ff1247 	O2 Eqv Ratio 1->8
		0xff1257 	CO2 in G/KM
		0xff1258 	CO2 in G/KM average
		0xff125a 	Fuel rate
		0xff125b 	Fuel cost (trip)
		0xff125c 	Fuel used (trip)
		0xff1255 	Torque RPM component
		0xff1256 	HorsePower RPM component
		0xff124d 	Commanded AFR
		0xff1249 	Measured AFR 
	*/

interface ITorqueService {

   /**
    * Get the API version (currently 1)
    */
   int getVersion();

   /**
    * Get the most recent value stored for the given PID.  This will return immediately whether or not data exists.
    * @param triggersDataRefresh Cause the data to be re-requested from the ECU
    */
   float getValueForPid(long pid, boolean triggersDataRefresh);

   /**
    * Get a textual, long description regarding the PID, already translated (when translation is implemented)
    */
   String getDescriptionForPid(long pid);
   
   /**
    * Get the shortname of the PID
    */
   String getShortNameForPid(long pid);

   /**
    * Get the Si unit in string form for the PID, if no Si unit is available, a textual-description is returned instead.
    */
   String getUnitForPid(long pid);
   
   /**
    * Get the minimum value expected for this PID
    */
   float getMinValueForPid(long pid);
   
   /**
    * Get the maximum value expected for this PId
    */
   float getMaxValueForPid(long pid);
   
   /**
    * Returns a list of currently 'active' PIDs. This list will change often.
    *
    * PIDs are stored as hex values, so '0x01, 0x0D' (vehicle speed) is the equivalent:  Integer.parseInt("010D",16);
    */
   long[] getListOfActivePids();

}
