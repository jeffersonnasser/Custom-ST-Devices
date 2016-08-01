/**
 *  Spruce Scheduler Pre-release V2.52 7/14/2016
 *
 *	
 *  Copyright 2015 Plaid Systems
 *
 *  Licensed under the Apache License, Version 2.0 (the "License"); you may not use this file except
 *  in compliance with the License. You may obtain a copy of the License at:
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 *  Unless required by applicable law or agreed to in writing, software distributed under the License is distributed
 *  on an "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. See the License
 *  for the specific language governing permissions and limitations under the License.
 *
 
-------v2.51---------------------
 schedule function changed so runIn does not overwrite and cancel schedule
 -ln 769 schedule cycleOn-> checkOn
 -ln 841 checkOn function
 -ln 863 state.run = false
 
-------Fixes
 -changed weather from def to Map
 -ln 968 if(runnowmap) -> pumpmap
 
-------Fixes V2.2-------------
-History log messages condensed
-Seasonal adjustment redefined -> weekly & daily
-Learn mode redefined
-No Learn redefined to operate any available days
-ZoneSettings page redefined -> required to setup zones
-Weather rain updated to fix error with some weather stations
-Contact time delay added
-new plants moisture and season redefined
*
*
-------Fixes V2.1-------------
-Many fixes, code cleanup by Jason C
-open fields leading to unexpected errors
-setting and summary improvements
-multi controller support
-Day to run mapping
-Contact delays optimized
-Warning notification added
-manual start subscription added
 *
 */
 
definition(
    name: "Spruce Scheduler v2.52",
    namespace: "plaidsystems",
    author: "Plaid Systems",
    description: "Spruce automatic water scheduling app v2.52",
    category: "Green Living",
    iconUrl: "http://www.plaidsystems.com/smartthings/st_spruce_leaf_250f.png",
    iconX2Url: "http://www.plaidsystems.com/smartthings/st_spruce_leaf_250f.png",
    iconX3Url: "http://www.plaidsystems.com/smartthings/st_spruce_leaf_250f.png")    
 
preferences {
    page(name: "startPage")
    page(name: "autoPage")
    page(name: "zipcodePage")
    page(name: "weatherPage")
    page(name: "globalPage")
    page(name: "contactPage")
    page(name: "delayPage")
    page(name: "zonePage")    

	page(name: "zoneSettingsPage")
    page(name: "zoneSetPage")
    page(name: "plantSetPage")
    page(name: "sprinklerSetPage")
    page(name: "optionSetPage")
    
    //found at bottom - transition pages
    page(name: "zoneSetPage1")
    page(name: "zoneSetPage2")
    page(name: "zoneSetPage3")
    page(name: "zoneSetPage4")
    page(name: "zoneSetPage5")
    page(name: "zoneSetPage6")
    page(name: "zoneSetPage7")
    page(name: "zoneSetPage8")
    page(name: "zoneSetPage9")
    page(name: "zoneSetPage10")
    page(name: "zoneSetPage11")
    page(name: "zoneSetPage12")
    page(name: "zoneSetPage13")
    page(name: "zoneSetPage14")
    page(name: "zoneSetPage15")
    page(name: "zoneSetPage16") 
}
 
def startPage(){
    dynamicPage(name: "startPage", title: "Spruce Smart Irrigation setup V2.52", install: true, uninstall: true)
    {                      
            section(""){
            href(name: "globalPage", title: "Schedule settings", required: false, page: "globalPage",
                image: "http://www.plaidsystems.com/smartthings/st_settings.png",                
                description: "Schedule: ${enableString()}\nWatering Time: ${startTimeString()}\nDays:${daysString()}\nNotifications:\n${notifyString()}")
             
            }
             
            section(""){            
            href(name: "weatherPage", title: "Weather Settings", required: false, page: "weatherPage",
                image: "http://www.plaidsystems.com/smartthings/st_rain_225_r.png",
                description: "Weather from: ${zipString()}\nSeasonal Adjust: ${seasonalAdjString()}")
            }
             
            section(""){            
            href(name: "zonePage", title: "Zone summary and setup", required: false, page: "zonePage",
                image: "http://www.plaidsystems.com/smartthings/st_zone16_225.png",
                description: "${getZoneSummary()}")
            }
             
            section(""){            
            href(name: "delayPage", title: "Valve and Contact delays", required: false, page: "delayPage",
                image: "http://www.plaidsystems.com/smartthings/st_timer.png",
                description: "Valve Delay: ${pumpDelayString()} s\nContact Sensor: ${contactSensorString()}\nSchedule Sync: ${syncString()}")
            }
            section(""){
                href title: "Spruce Irrigation Knowledge Base", //page: "customPage",
                description: "Explore our knowledge base for more information on Spruce and Spruce sensors.  Contact from is also available here.",
                required: false, style:"embedded",             
                image: "http://www.plaidsystems.com/smartthings/st_spruce_leaf_250f.png",
                url: "http://support.spruceirrigation.com"
            }  
       }
}
 
def globalPage() {
    dynamicPage(name: "globalPage", title: "") {
        section("Spruce schedule Settings") {
                label title: "Schedule Name:", description: "Name this schedule", required: false                
                input "switches", "capability.switch", title: "Spruce Irrigation Controller:", description: "Select a Spruce controller", required: true, multiple: false
		}        


        section("Program Scheduling"){
            input "enable", "bool", title: "Enable watering:", defaultValue: 'true', metadata: [values: ['true', 'false']]
            input "enableManual", "bool", title: "Enable this schedule for manual start, only 1 schedule should be enabled for manual start at a time!", defaultValue: 'true', metadata: [values: ['true', 'false']]
            input "startTime", "time", title: "Watering start time", required: true            
            paragraph image: "http://www.plaidsystems.com/smartthings/st_calander.png",
                      title: "Selecting watering days", 
                      "Selecting watering days is optional. Spruce will optimize your watering schedule automatically. If your area has water restrictions or you prefer set days, select the days to meet your requirements. "
			input (name: "days", type: "enum", title: "Water only on these days...", required: false, multiple: true,
            metadata: [values: ['Monday', 'Tuesday', 'Wednesday', 'Thursday', 'Friday', 'Saturday', 'Sunday', 'Even', 'Odd']])            
		}

        section("Push Notifications") {
        		input("recipients", "contact", title: "Send push notifications to", required: false)
                input (name: "notify", type: "enum", title: "Select what push notifications to receive.", required: false, multiple: true,
                metadata: [values: ['Warnings', 'Daily', 'Weekly', 'Weather', 'Moisture']])                
        } 
         
    }
}
 
 
def weatherPage() {
    dynamicPage(name: "weatherPage", title: "Weather settings") {
       section("Location to get weather forecast and conditions:") {
            href(name: "hrefWithImage", title: "${zipString()}", page: "zipcodePage",
             description: "Set local weather station",
             required: false,             
             image: "http://www.plaidsystems.com/smartthings/rain.png")             
             
            input "rainDelay", "decimal", title: "inches of rain that will delay watering, default: 0.2", required: false
            input "isSeason", "bool", title: "Enable Seasonal Weather Adjustment:", metadata: [values: ['true', 'false']]
        }                
    }    
}
 
def zipcodePage() {
    return dynamicPage(name: "zipcodePage", title: "Spruce weather station setup") {
        section(""){input "zipcode", "text", title: "Zipcode or WeatherUnderground station id. Default value is current location.", defaultValue: "${location.zipCode}", required: false, submitOnChange: true
                }
         
        section(""){href title: "Search WeatherUnderground.com for weather stations",
             description: "After page loads, select Change Station for a list of weather stations.  You will need to copy the station code into the zipcode field above",
             required: false, style:"embedded",             
             image: "http://www.plaidsystems.com/smartthings/wu.png",
             url: "http://www.wunderground.com/q/${location.zipCode}"
             }
    }
}
 
private startTimeString(){  
    def newtime = "${settings["startTime"]}"
    if ("${settings["startTime"]}" == "null") return "Please set!"   
    else return "${hhmm(newtime)}"    
}

def enableString(){
	if(enable && enableManual) return "On, Manual Enabled"
    else if (enable) return "On"
    return "Off"
}

def contactSensorString(){
	if(contact) return "${contact} \n  Delay: ${contactDelay} mins"
    return "None"
}

def seasonalAdjString(){
	if(isSeason) return "${isSeason}"
    return "False"
}

def syncString(){
	if(sync) return "${sync}"
    return "None"
}

def notifyString(){
	def notifyString = ""
	if("${settings["notify"]}" != "null") {
      if (notify.contains('Weekly')) notifyString = "${notifyString} Weekly"
      if (notify.contains('Daily')) notifyString = "${notifyString} Daily"
      if (notify.contains('Weather')) notifyString = "${notifyString} Weather"
      if (notify.contains('Warnings')) notifyString = "${notifyString} Warnings"
      if (notify.contains('Moisture')) notifyString = "${notifyString} Moisture"
   }
   if(notifyString == "")
   	  notifyString = " None"
   return notifyString
}

def daysString(){
	def daysString = ""
    if ("${settings["days"]}" != "null") {
    	if(days.contains('Even') || days.contains('Odd')) {
        	if (days.contains('Even')) daysString = "${daysString} Even"
      		if (days.contains('Odd')) daysString = "${daysString} Odd"
        } else {
            if (days.contains('Monday')) daysString = "${daysString} M"
        	if (days.contains('Tuesday')) daysString = "${daysString} Tu"
        	if (days.contains('Wednesday')) daysString = "${daysString} W"
        	if (days.contains('Thursday')) daysString = "${daysString} Th"
        	if (days.contains('Friday')) daysString = "${daysString} F"
        	if (days.contains('Saturday')) daysString = "${daysString} Sa"
        	if (days.contains('Sunday')) daysString = "${daysString} Su"
        }
    }
    if(daysString == "")
   	  daysString = " Any"
    return daysString
}
 
    
private hhmm(time, fmt = "h:mm a"){
    def t = timeToday(time, location.timeZone)
    def f = new java.text.SimpleDateFormat(fmt)
    f.setTimeZone(location.timeZone ?: timeZone(time))
    f.format(t)
}
 
def pumpDelayString(){
    if ("${settings["pumpDelay"]}" == "null") return "5"
    else return "${settings["pumpDelay"]}"
}
 
def delayPage() {
    dynamicPage(name: "delayPage", title: "Additional Options") {
        section(""){
            paragraph image: "http://www.plaidsystems.com/smartthings/st_timer.png",
                      title: "Pump and Master valve delay",
                      required: false,
                      "Setting a delay is optional, default is 0.  If you have a pump that feeds water directly into your valves, set this to 0. To fill a tank or build pressure, you may increase the delay.\nStart->Pump On->delay->Valve On->Valve Off->delay"
        }
        section("") {
                input "pumpDelay", "number", title: "Set a delay in seconds?", defaultValue: '0', required: false
        }
        section(""){
            paragraph image: "http://www.plaidsystems.com/smartthings/st_pause.png",
                      title: "Contact delays",
                      required: false,
                      "Selecting contacts is optional. When a selected contact sensor is opened, water immediately stops and will not resume until closed.  Caution: if a contact is set and left open, the watering program will never run."
        }
        section("") {
            input name: "contact", title: "Select water delay contacts", type: "capability.contactSensor", multiple: true, required: false            

			input "contactDelay", "number", title: "How many minutes delay after contact is closed?", defaultValue: '1', required: false
        }
        section(""){
            paragraph image: "http://www.plaidsystems.com/smartthings/st_spruce_controller_250.png",
                      title: "Controller Sync",
                      required: false,
                      "For multiple controllers only.  This schedule will wait for the selected controller to finish. Do not set with a single controller!"
        			  input "sync", "capability.switch", title: "Select Master Controller", description: "Only use this setting with multiple controllers", required: false, multiple: false
        }
    }
}
 
def zonePage() {    
    dynamicPage(name: "zonePage", title: "Zone setup", install: false, uninstall: false) {
		section("") {
            href(name: "hrefWithImage", title: "Zone configuration", page: "zoneSettingsPage",
             description: "${zoneString()}",
             required: false,             
             image: "http://www.plaidsystems.com/smartthings/st_spruce_leaf_250f.png")
        }

		if (zoneActive("1")){
        section(""){
            href(name: "z1Page", title: "1: ${getname("1")}", required: false, page: "zoneSetPage1",
            	image: "${getimage("1")}",                
            	description: "${display("1")}" )
            }
        }
        if (zoneActive("2")){
        section(""){
            href(name: "z2Page", title: "2: ${getname("2")}", required: false, page: "zoneSetPage2",
            	image: "${getimage("2")}",
            	description: "${display("2")}" )
            }
        }
        if (zoneActive("3")){
        section(""){
            href(name: "z3Page", title: "3: ${getname("3")}", required: false, page: "zoneSetPage3",
            	image: "${getimage("3")}",
            	description: "${display("3")}" )
            }
        }
        if (zoneActive("4")){
        section(""){
            href(name: "z4Page", title: "4: ${getname("4")}", required: false, page: "zoneSetPage4",
            	image: "${getimage("4")}",
            	description: "${display("4")}" )
            }
        }
        if (zoneActive("5")){
        section(""){
            href(name: "z5Page", title: "5: ${getname("5")}", required: false, page: "zoneSetPage5",
            	image: "${getimage("5")}",
            	description: "${display("5")}" )
            }
        }
        if (zoneActive("6")){
        section(""){
            href(name: "z6Page", title: "6: ${getname("6")}", required: false, page: "zoneSetPage6",
            	image: "${getimage("6")}",
            	description: "${display("6")}" )
            }
        }
        if (zoneActive("7")){    
        section(""){
            href(name: "z7Page", title: "7: ${getname("7")}", required: false, page: "zoneSetPage7",
            	image: "${getimage("7")}",
            	description: "${display("7")}" )
            }
        }
        if (zoneActive("8")){
        section(""){
            href(name: "z8Page", title: "8: ${getname("8")}", required: false, page: "zoneSetPage8",
            	image: "${getimage("8")}",
            	description: "${display("8")}" )
            }
        }
        if (zoneActive("9")){
        section(""){
            href(name: "z9Page", title: "9: ${getname("9")}", required: false, page: "zoneSetPage9",
            	image: "${getimage("9")}",
                description: "${display("9")}" )
            }
        }
        if (zoneActive("10")){
        section(""){
            href(name: "z10Page", title: "10: ${getname("10")}", required: false, page: "zoneSetPage10",
            	image: "${getimage("10")}",
                description: "${display("10")}" )
            }
        }
        if (zoneActive("11")){
        section(""){
            href(name: "z11Page", title: "11: ${getname("11")}", required: false, page: "zoneSetPage11",
            	image: "${getimage("11")}",
                description: "${display("11")}" )
            }
        }
        if (zoneActive("12")){
        section(""){
            href(name: "z12Page", title: "12: ${getname("12")}", required: false, page: "zoneSetPage12",
            	image: "${getimage("12")}",
                description: "${display("12")}" )
            }
        }
        if (zoneActive("13")){
        section(""){
            href(name: "z13Page", title: "13: ${getname("13")}", required: false, page: "zoneSetPage13",
            	image: "${getimage("13")}",
                description: "${display("13")}" )
            }
        }
        if (zoneActive("14")){
        section(""){
            href(name: "z14Page", title: "14: ${getname("14")}", required: false, page: "zoneSetPage14",
            	image: "${getimage("14")}",
                description: "${display("14")}" )
            }
        }
        if (zoneActive("15")){
        section(""){
            href(name: "z15Page", title: "15: ${getname("15")}", required: false, page: "zoneSetPage15",
            	image: "${getimage("15")}",
                description: "${display("15")}" )
            }
        }
        if (zoneActive("16")){
        section(""){
            href(name: "z16Page", title: "16: ${getname("16")}", required: false, page: "zoneSetPage16",
            	image: "${getimage("16")}",
                description: "${display("16")}" )
            }
        }        

    }
}

def zoneActive(z){
	if (!zoneNumber) return false
    if (zoneNumber.contains(z)) return true
    return false
}

def zoneString(){
	def numberString = "Add zones to setup"
    if (zoneNumber) numberString = "Zones enabled: " + "${zoneNumber}"
    if (learn) numberString += "\nSensor mode: Adaptive"
    else numberString += "\nSensor mode: Delay"
    return numberString
    }
    
def zoneSettingsPage(){
	dynamicPage(name: "zoneSettingsPage", title: "Zone Configuration") {
        	section(""){
        	//input (name: "zoneNumber", type: "number", title: "Enter number of zones to configure?",description: "How many valves do you have? 1-16", required: true)//, defaultValue: 16)
            input "zoneNumber", "enum", title: "Select zones to configure", multiple: true, metadata: [values: ['1','2','3','4','5','6','7','8','9','10','11','12','13','14','15','16']]
            input "gain", "number", title: "Increase or decrease all water times by this %, enter a negative or positive value, Default: 0", required: false
			paragraph image: "http://www.plaidsystems.com/smartthings/st_sensor_200_r.png",
                      title: "Moisture sensor adapt mode",                      
                      "Adaptive mode: Watering times will be adjusted based on the assigned moisture sensor.\n\nNo Adaptive mode: Zones with moisture sensors will water on any available days when the low moisture setpoint has been reached."
         	input "learn", "bool", title: "Enable Adaptive Moisture Control (with moisture sensors)", metadata: [values: ['true', 'false']]
            }
     }
}

def zoneSetPage(){    
    dynamicPage(name: "zoneSetPage", title: "Zone ${state.app} Setup") {
        section(""){
            paragraph image: "http://www.plaidsystems.com/smartthings/st_${state.app}.png",             
            title: "Current Settings",            
            "${display("${state.app}")}"        
            }
        section(""){
                input "name${state.app}", "text", title: "Zone name?", required: false, defaultValue: "Zone ${state.app}"
                }
        /*section(""){
                input "schNum${state.app}", "enum", title: "Add this zone to the selected schedules", required: false, multiple: true, metadata: [values: ['1']]
                }*/
        section(""){            
            href(name: "tosprinklerSetPage", title: "Sprinkler type: ${setString("zone")}", required: false, page: "sprinklerSetPage",
                image: "${getimage("${settings["zone${state.app}"]}")}",           
                description: "Set sprinkler nozzle type or turn zone off")
             }
        section(""){            
            href(name: "toplantSetPage", title: "Landscape Select: ${setString("plant")}", required: false, page: "plantSetPage",
                image: "${getimage("${settings["plant${state.app}"]}")}",
                description: "Set landscape type")
            }  
         
        section(""){            
            href(name: "tooptionSetPage", title: "Options: ${setString("option")}", required: false, page: "optionSetPage",
                image: "${getimage("${settings["option${state.app}"]}")}",
                description: "Set watering options") 
            }            
        section(""){
            paragraph image: "http://www.plaidsystems.com/smartthings/st_sensor_200_r.png",
                      title: "Moisture sensor settings",                      
                      "Select a soil moisture sensor to monitor and control watering.  The soil moisture target value is set to a default value but can be adjusted to tune watering"
         
                input "sensor${state.app}", "capability.relativeHumidityMeasurement", title: "Select moisture sensor?", required: false, multiple: false
         
                input "sensorSp${state.app}", "number", title: "Minimum moisture sensor target value, Setpoint: ${getDrySp("${state.app}")}", required: false
        }
        section(""){
            paragraph image: "http://www.plaidsystems.com/smartthings/st_timer.png",
                      title: "Optional: Enter total watering time per week", 
                      "This value will replace the calculated time from other settings"
         
                input "minWeek${state.app}", "number", title: "Water time per week (minutes).\nDefault: 0 = autoadjust", required: false
                
                input "perDay${state.app}", "number", title: "Guideline value for time per day, this divides minutes per week into watering days. Default: 20", defaultValue: '20', required: false
        }
 
    }
}    

def setString(i){
	if (i == "zone"){
    	if (settings["zone${state.app}"] != null) return "${settings["zone${state.app}"]}"
        else return "Not Set"
        }
	if (i == "plant"){
    	if (settings["plant${state.app}"] != null) return "${settings["plant${state.app}"]}"
        else return "Not Set"
        }
	if (i == "option"){
    	if (settings["option${state.app}"] != null) return "${settings["option${state.app}"]}"
        else return "Not Set"
        }
}

def plantSetPage(){ 
    dynamicPage(name: "plantSetPage", title: "${settings["name${state.app}"]} Landscape Select") {
        section(""){
            paragraph image: "http://www.plaidsystems.com/img/st_${state.app}.png",             
                title: "${settings["name${state.app}"]}",
                "Current settings ${display("${state.app}")}"
                 
            input "plant${state.app}", "enum", title: "Landscape", multiple: false, required: false, submitOnChange: true, metadata: [values: ['Lawn', 'Garden', 'Flowers', 'Shrubs', 'Trees', 'Xeriscape', 'New Plants']]
            }        
        section(""){
            paragraph image: "http://www.plaidsystems.com/smartthings/st_lawn_200_r.png",             
            title: "Lawn",            
            "Select Lawn for typical grass applications"
             
            paragraph image: "http://www.plaidsystems.com/smartthings/st_garden_225_r.png",             
            title: "Garden",            
            "Select Garden for vegetable gardens"
            
            paragraph image: "http://www.plaidsystems.com/smartthings/st_flowers_225_r.png",             
            title: "Flowers",            
            "Select Flowers for beds with smaller seasonal plants"
             
            paragraph image: "http://www.plaidsystems.com/smartthings/st_shrubs_225_r.png",             
            title: "Shrubs",            
            "Select Shrubs for beds with larger established plants"
           
            paragraph image: "http://www.plaidsystems.com/smartthings/st_trees_225_r.png",             
            title: "Trees",            
            "Select Trees for deep rooted areas without other plants"
           
            paragraph image: "http://www.plaidsystems.com/smartthings/st_xeriscape_225_r.png",             
            title: "Xeriscape",            
            "Reduces water for native or drought tolorent plants"
            
            paragraph image: "http://www.plaidsystems.com/smartthings/st_newplants_225_r.png",             
            title: "New Plants",            
            "Increases watering time per week and reduces automatic adjustments to help establish new plants. No weekly seasonal adjustment and moisture setpoint set to 40."
            }
     }
}
 
def sprinklerSetPage(){
    dynamicPage(name: "sprinklerSetPage", title: "${settings["name${state.app}"]} Sprinkler Select") {
        section(""){
            paragraph image: "http://www.plaidsystems.com/img/st_${state.app}.png",             
            title: "${settings["name${state.app}"]}",
            "Current settings ${display("${state.app}")}"
            input "zone${state.app}", "enum", title: "Sprinkler Type", multiple: false, required: false, defaultValue: 'Off', metadata: [values: ['Off', 'Spray', 'Rotor', 'Drip', 'Master Valve', 'Pump']]
                         
            }
        section(""){
            paragraph image: "http://www.plaidsystems.com/smartthings/st_spray_225_r.png",             
            title: "Spray",            
            "Spray sprinkler heads spray a fan of water over the lawn. The water is applied evenly and can be turned on for a shorter duration of time."
             
            paragraph image: "http://www.plaidsystems.com/smartthings/st_rotor_225_r.png",             
            title: "Rotor",            
            "Rotor sprinkler heads rotate, spraying a stream over the lawn.  Because they move back and forth across the lawn, they require a longer water period."
             
            paragraph image: "http://www.plaidsystems.com/smartthings/st_drip_225_r.png",             
            title: "Drip",            
            "Drip lines or low flow emitters water slowely to minimize evaporation, because they are low flow, they require longer watering periods."
             
            paragraph image: "http://www.plaidsystems.com/smartthings/st_master_225_r.png",             
            title: "Master",            
            "Master valves will open before watering begins.  Set the delay between master opening and watering in delay settings."
             
            paragraph image: "http://www.plaidsystems.com/smartthings/st_pump_225_r.png",             
            title: "Pump",            
            "Attach a pump relay to this zone and the pump will turn on before watering begins.  Set the delay between pump start and watering in delay settings."
            }
        }
}
 
def optionSetPage(){
    dynamicPage(name: "optionSetPage", title: "${settings["name${state.app}"]} Options") {
        section(""){
            paragraph image: "http://www.plaidsystems.com/img/st_${state.app}.png",             
            title: "${settings["name${state.app}"]}",
            "Current settings ${display("${state.app}")}"
            input "option${state.app}", "enum", title: "Options", multiple: false, required: false, defaultValue: 'Cycle 2x', metadata: [values: ['Slope', 'Sand', 'Clay', 'No Cycle', 'Cycle 2x', 'Cycle 3x']]    
        }
        section(""){
            paragraph image: "http://www.plaidsystems.com/smartthings/st_slope_225_r.png",             
            title: "Slope",            
            "Slope sets the sprinklers to cycle 3x, each with a short duration to minimize runoff"
             
            paragraph image: "http://www.plaidsystems.com/smartthings/st_sand_225_r.png",             
            title: "Sand",            
            "Sandy soil drains quickly and requires more frequent but shorter intervals of water."
             
            paragraph image: "http://www.plaidsystems.com/smartthings/st_clay_225_r.png",             
            title: "Clay",            
            "Clay sets the sprinklers to cycle 2x, each with a short duration to maximize absorption"
             
            paragraph image: "http://www.plaidsystems.com/smartthings/st_cycle1x_225_r.png",             
            title: "No Cycle",            
            "The sprinklers will run for 1 long duration"
             
            paragraph image: "http://www.plaidsystems.com/smartthings/st_cycle2x_225_r.png",             
            title: "Cycle 2x",            
            "Cycle 2x will break the water period up into 2 shorter cycles to help minimize runoff and maximize adsorption"
             
            paragraph image: "http://www.plaidsystems.com/smartthings/st_cycle3x_225_r.png",             
            title: "Cycle 3x",            
            "Cycle 3x will break the water period up into 3 shorter cycles to help minimize runoff and maximize adsorption"
            }
        }
}
 
def setPage(i){
    if (i != "null") state.app = i
    return state.app
}

def getaZoneSummary(zone){
  def daysString = ""
  def dpw = initDPW(zone)
  def runTime = calcRunTime(initTPW(zone), dpw)
  if ( !learn && (settings["sensor${zone}"] != null) ) {
  	 daysString = "if Moisture is low on: "
     dpw = daysAvailable()
     }  
  if (days && (days.contains('Even') || days.contains('Odd'))) {
    if(dpw == 1) daysString = "Every 8 days"
    if(dpw == 2) daysString = "Every 4 days"
    if(dpw == 4) daysString = "Every 2 days"
    if(days.contains('Even') && days.contains('Odd')) daysString = "any day"
  } 
  else {
     def int[] dpwMap = [0,0,0,0,0,0,0]
     dpwMap = getDPWDays(dpw)
     daysString += getRunDays(dpwMap)
  }  
  return "${zone}: ${runTime} minutes, ${daysString}"
}

def getZoneSummary(){
 	def summary = ""
    if (learn) summary = "Moisture Learning enabled"
    else summary = "Moisture Learning disabled"
         
    def zone = 1
    createDPWMap()
    while(zone <= 16) {	  
      def zoneSum = getaZoneSummary(zone)
      if (nozzle(zone) == 4) summary = "${summary}\n${zone}: ${settings["zone${zone}"]}"
      else if ( "${runTime}" != "0" && "${initDPW(zone)}" != "0" && zoneActive(zone.toString()) ) summary = "${summary}\n${zoneSum}"
      zone++
    }
    if(summary == "") return zoneString()	//"Setup all 16 zones"
    
    return summary
}
 
def display(i){
    def displayString = ""
    def dpw = initDPW(i)
    def runTime = calcRunTime(initTPW(i), dpw)
    if ("${settings["zone${i}"]}" != "null") displayString += "${settings["zone${i}"]} : "
    if ("${settings["plant${i}"]}" != "null") displayString += "${settings["plant${i}"]} : "
    if ("${settings["option${i}"]}" != "null") displayString += "${settings["option${i}"]} : "
    if ("${settings["sensor${i}"]}" != "null") displayString += "${settings["sensor${i}"]} : "
    if ("${runTime}" != "0" && "${dpw}" != "0") displayString += "${runTime} minutes, ${dpw} days per week"
    return "${displayString}"
}

def getimage(i){
    if ("${settings["zone${i}"]}" == "Off") return "http://www.plaidsystems.com/smartthings/off2.png"   
    else if ("${settings["zone${i}"]}" == "Master Valve") return "http://www.plaidsystems.com/smartthings/master.png"
    else if ("${settings["zone${i}"]}" == "Pump") return "http://www.plaidsystems.com/smartthings/pump.png"
    else if ("${settings["plant${i}"]}" != "null" && "${settings["zone${i}"]}" != "null")// && "${settings["option${i}"]}" != "null")
    	i = "${settings["plant${i}"]}"
     
    switch("${i}"){
        case "null":
            return "http://www.plaidsystems.com/smartthings/off2.png"
        case "Off":
            return "http://www.plaidsystems.com/smartthings/off2.png"
        case "Lawn":
            return "http://www.plaidsystems.com/smartthings/st_lawn_200_r.png"
        case "Garden":
            return "http://www.plaidsystems.com/smartthings/st_garden_225_r.png"
        case "Flowers":
            return "http://www.plaidsystems.com/smartthings/st_flowers_225_r.png"
        case "Shrubs":
            return "http://www.plaidsystems.com/smartthings/st_shrubs_225_r.png"
        case "Trees":
            return "http://www.plaidsystems.com/smartthings/st_trees_225_r.png"
        case "Xeriscape":
            return "http://www.plaidsystems.com/smartthings/st_xeriscape_225_r.png"
        case "New Plants":
            return "http://www.plaidsystems.com/smartthings/st_newplants_225_r.png"
        case "Spray":
            return "http://www.plaidsystems.com/smartthings/st_spray_225_r.png"
        case "Rotor":
            return "http://www.plaidsystems.com/smartthings/st_rotor_225_r.png"
        case "Drip":
            return "http://www.plaidsystems.com/smartthings/st_drip_225_r.png"
        case "Master Valve":
            return "http://www.plaidsystems.com/smartthings/st_master_225_r.png"
        case "Pump":
            return "http://www.plaidsystems.com/smartthings/st_pump_225_r.png"
        case "Slope":
            return "http://www.plaidsystems.com/smartthings/st_slope_225_r.png"
        case "Sand":
            return "http://www.plaidsystems.com/smartthings/st_sand_225_r.png"
        case "Clay":
            return "http://www.plaidsystems.com/smartthings/st_clay_225_r.png"
        case "No Cycle":
            return "http://www.plaidsystems.com/smartthings/st_cycle1x_225_r.png"
        case "Cycle 2x":
            return "http://www.plaidsystems.com/smartthings/st_cycle2x_225_r.png"
        case "Cycle 3x":
            return "http://www.plaidsystems.com/smartthings/st_cycle3x_225_r.png"
        default:
            return "http://www.plaidsystems.com/smartthings/off2.png"            
        }
    }
 
def getname(i) { 
    if ("${settings["name${i}"]}" != "null") return "${settings["name${i}"]}"   
    else return "Zone $i"
}

def zipString() {
    if (!zipcode) return "${location.zipCode}"
    //add pws for correct weatherunderground lookup
    if (!zipcode.isNumber()) return "pws:${zipcode}"
    else return "${zipcode}"
}
         
//app install
def installed() {
    state.dpwMap = [0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0]
    state.tpwMap = [0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0]
    state.Rain = [0,0,0,0,0,0,0]    
    state.daycount = [0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0]    
    
    log.debug "Installed with settings: ${settings}"
    installSchedule()
}
 
def updated() {    
    log.debug "Installed with settings: ${settings}"
    installSchedule()    
}
 
def installSchedule(){
	state.seasonAdj = 100
    state.weekseasonAdj = 0
    unsubscribe()
    unschedule()
    state.run = false
    int randomOffset = 0
    
    if (enableManual) subscribe(switches, "switch.programOn", manualStart)
    else randomOffset = getRandomNumber(1) * 1000	//random number added to start time so multiple schedules do not have identical time
    if (switches && startTime && enable){    	
        def checktime = timeToday(startTime, location.timeZone).getTime() + randomOffset
    	schedule(checktime, preCheck)	//check weather & Days
        writeSettings()
        note("schedule", "${app.label} schedule set to start at ${startTimeString()}", "w")
    }
    else note("disable", "Automatic watering turned off or incomplete setup.", "w")
}
 
//write initial zone settings to device at install/update
def writeSettings(){    
    if(!state.tpwMap) state.tpwMap = [0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0]
    if(!state.dpwMap) state.dpwMap = [0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0]    
    if(!state.seasonAdj) state.seasonAdj = 0
    if(!state.weekseasonAdj) state.weekseasonAdj = 0
    setSeason()	    
}

//get day of week integer
def getWeekDay(day)
{
	def weekdays = ['Monday', 'Tuesday', 'Wednesday', 'Thursday', 'Friday', 'Saturday', 'Sunday']
	def mapDay = [Monday:1, Tuesday:2, Wednesday:3, Thursday:4, Friday:5, Saturday:6, Sunday:7]  
	if(day && weekdays.contains(day)) {
    	return mapDay.get(day)
    }
	def today = new Date().format("EEEE", location.timeZone)
	return mapDay.get(today)
}

// Get string of run days from dpwMap
def getRunDays(day1,day2,day3,day4,day5,day6,day7)
{
    def str = ""
    if(day1)
    	str += "M"
    if(day2)
    	str += "T"
    if(day3)
    	str += "W"
    if(day4)
    	str += "Th"
    if(day5)
    	str += "F"
    if(day6)
    	str += "Sa"
    if(day7)
    	str += "Su"
    if(string == "")
    	str = "0 Days/week"
    return str
}

def getRandomNumber(int num){
    Random random = new Random()
    return random.nextInt(40 ** num)
}

//start manual schedule
def manualStart(evt){
	if (enableManual && state.run == false){
        def runNowMap = []
        runNowMap = cycleLoop(0)    
        if (runNowMap)
        { 
            state.run = true        
            runNowMap = "${app.label} manually started, watering in 1 minute:\n" + runNowMap
            note("active", "${runNowMap}", "d")                      
            runIn(60, cycleOn)   //start water program
        }

        else {
            switches.programOff()
            state.run = false        
            note("skipping", "Check schedule setup.", "d")
        }
    }
}

//is another schedule running
def busy(){
	log.debug "Current status: ${switches.currentValue('switch')}"
    if (state.run == true){
    	note("active", "${app.label} already running, skipping additional start", "d")
    	return true
        }
    else if (switches.currentValue('switch').contains('off') && !switches.currentValue('status').contains('active') && !switches.currentValue('status').contains('season') && !switches.currentValue('status').contains('pause') && !switches.currentValue('status').contains('moisture')) return false
    else {
    	subscribe switches, "switch.off", busyOff      
    	note("active", "Another schedule running, waiting to start ${app.label}", "d")
        return true
    }
}

def busyOff(evt){
	unsubscribe(switches)
    //note("pause", "${app.label} resuming pre-check after another schedule finished.", "d")
    runIn(10, preCheck)   
}

//run check every day
def preCheck(){
	if (!busy()){
        note("active", "${app.label} starting pre-check.", "d")
        state.daycount = adddays()
        state.run = true
        if (isWeather() == false) checkRunMap()
        else {
            switches.programOff()
            state.run = false     
            note("skipping", "${app.label} not scheduled for today.", "d")
            }
	}	
}

//start water program
def cycleOn(){       
    unschedule(cycleOn)
    unsubscribe()    
            
    if (sync != null && ( !sync.currentValue('switch').contains('off') || sync.currentValue('status').contains('active') || sync.currentValue('status').contains('pause') || sync.currentValue('status').contains('season') ) ){
        subscribe sync, "switch.off", syncOn
        note("pause", "waiting for $sync to complete before starting schedule", "w")
    }
    else if (contact == null || !contact.currentValue('contact').contains('open')) {
    	subscribe switches, "switch.off", cycleOff
        subscribe contact, "contact.open", doorOpen                
        resume()
       	}
    else {
    	subscribe switches, "switch.off", cycleOff
        note("pause", "$contact opened $switches paused watering", "w")
    }
}
 
//when switch reports off, watering program is finished
def cycleOff(evt){
	state.run = false    
    unsubscribe(contact)
    unsubscribe(switches)    
    if (enableManual) subscribe(switches, "switch.programOn", manualStart)
    if (contact == null || !contact.currentValue('contact').contains('open')){    
    	note("finished", "finished watering for today", "d")
    }    
}

//run check each day at scheduled time
def checkRunMap(){	
    // Create weekly water summary, if requested, on Sunday	
    if(notify && notify.contains('Weekly') && (getWeekDay() == 7))
    {
    	def zone = 1
        def zoneSummary = ""
        while(zone <= 16) {
        	if(settings["zone${zone}"] != null && settings["zone${zone}"] != 'Off' && nozzle(zone) != 4) {
               def sum = getaZoneSummary(zone)
			   zoneSummary = "${zoneSummary} ${sum}"
            }
            zone++
        }
        log.debug "Weekly water summary: ${zoneSummary}"
        send "Weekly water summary: ${zoneSummary}"
    }    
    
    //get & set watering times for today
    def runNowMap = []
    runNowMap = cycleLoop(1)        
    if (runNowMap)
    { 
        state.run = true            
        def runTime = now() + 60000
		log.debug runTime
        schedule(runTime, cycleOn)	//start water
        runNowMap = "${app.label} water will begin in 1 minute:\n" + runNowMap
        note("active", "${runNowMap}", "d")
    }
    else {
        state.run = false 
        switches.programOff()                   
        note("skipping", "No watering today.", "d")
    }
     
}
 
//get todays schedule
def cycleLoop(i)
{
    def zone = 1
    def cyc = 0
    def rtime = 0
    def timeMap = [:]
    def pumpMap = ""
    def runNowMap = ""
    def soilString = ""    

    while(zone <= 16)
    {
        rtime = 0
        //change to tpw(?)
        if( settings["zone${zone}"] != null && settings["zone${zone}"] != 'Off' && nozzle(zone) != 4 && zoneActive(zone.toString()) )
        {
		  // First check if we run this zone today, use either dpwMap or even/odd date
		  def dpw = getDPW(zone)          
          def runToday = 0
          if (days && (days.contains('Even') || days.contains('Odd'))) {
            def daynum = new Date().format("dd", location.timeZone)
            int dayint = Integer.parseInt(daynum)
            if(days.contains('Odd') && (dayint +1) % Math.round(31 / (dpw * 4)) == 0) runToday = 1
          	if(days.contains('Even') && dayint % Math.round(31 / (dpw * 4)) == 0) runToday = 1
          } else {
            def weekDay = getWeekDay()-1
            def dpwMap = getDPWDays(dpw)
            def today = dpwMap[weekDay]
            log.debug "Zone: ${zone} dpw: ${dpw} weekDay: ${weekDay} dpwMap: ${dpwMap} today: ${today}"
            runToday = dpwMap[weekDay]	//1 or 0
          }         
          //if no learn check moisture sensors on available days
          if (!learn && (settings["sensor${zone}"] != null) ) runToday = 1
          
          //manual=0 force run
          if (i == 0) runToday = 1
          
          if(runToday) 
          {
			def soil
            if (i == 0) soil = moisture(0)
            else soil = moisture(zone)
          	soilString += "${soil[1]}"

			// Run this zone if soil moisture needed or if it is a weekly
            // We run at least once a week and let moisture lower the time if needed
            if ( (soil[0] == 1 ) || (learn && dpw == 1) )
            	{
                state.daycount[zone-1] = 0
                cyc = cycles(zone)
                dpw = getDPW(zone)
                rtime = calcRunTime(getTPW(zone), dpw)                
                //daily weather adjust if no sensor
                if(isSeason && settings["sensor${zone}"] == null) rtime = Math.round(rtime / cyc * state.seasonAdj / 100)
                // runTime is total run time devided by num cycles
                else rtime = Math.round(rtime / cyc)                    
                runNowMap += "${settings["name${zone}"]}: ${cyc} x ${rtime} min\n"
                log.debug"Zone ${zone} Map: ${cyc} x ${rtime} min"
            	}
        	}
		}
        else state.daycount[zone-1] = 0
        if (nozzle(zone) == 4) pumpMap += "${settings["name${zone}"]}: ${settings["zone${zone}"]} on\n"
        timeMap."${zone+1}" = "${rtime}"
        zone++  
    }
	if (soilString) {
    	soilString = "Moisture Sensors:\n" + soilString
        note("moisture", "${soilString}","m")
        }
    if (!runNowMap) return runNowMap
    
    //send settings to Spruce Controller
    switches.settingsMap(timeMap,4002)
    def runTime = now() + 30000
	schedule(runTime, writeCycles)    
    return runNowMap += pumpMap    
}

//send cycle settings
def writeCycles(){
	def cyclesMap = [:]
    //add pumpdelay @ 1
    cyclesMap."1" = pumpDelayString()
    def zone = 1
    def cycle = 0	
    while(zone <= 17)
    {      
        if(nozzle(zone) == 4) cycle = 4
        else cycle = cycles(zone)
        //offset by 1, due to pumpdelay @ 1
        cyclesMap."${zone+1}" = "${cycle}"
        if (zone <= 16) {
        	state.tpwMap.putAt(zone-1, initTPW(zone))
            state.dpwMap.putAt(zone-1, initDPW(zone))            
        }
        zone++
    }
    switches.settingsMap(cyclesMap, 4001)
}

def resume(){
	switches.on()    
}

def syncOn(evt){
    note("active", "$sync complete, starting scheduled program", "w")
    cycleOn()
}

def doorOpen(evt){
    note("pause", "$contact opened $switches paused watering", "w")
    unsubscribe(switches)
    subscribe contact, "contact.closed", doorClosed
    switches.off()        
}
     
def doorClosed(evt){
    note("active", "$contact closed $switches will resume watering in $contactDelay minutes", "w")    
    runIn(contactDelay * 60, cycleOn)
}

//days since last run
def adddays(){
	def i = 0
    while(i <= 15){
    	state.daycount[i] = state.daycount[i] + 1        
        i++
    }    
	return state.daycount
}

//Initialize Days per week, based on TPW, perDay and daysAvailable settings
def initDPW(i){
	if(initTPW(i) > 0) {
    	def dpw
        def perDay = 20
        if(settings["perDay${i}"]) perDay = settings["perDay${i}"].toInteger()
    	dpw = Math.round(initTPW(i) / perDay)
    	if(dpw <= 1) return 1
		// 3 days per week not allowed for even or odd day selection
	    if(dpw == 3 && days && (days.contains('Even') || days.contains('Odd')) && !(days.contains('Even') && days.contains('Odd')))
			if(initTPW(i) / perDay < 3.0) return 2
			else return 4
    	if(daysAvailable() < dpw) return daysAvailable()
    	return dpw
    }
	return 0
}

// Get current days per week value, calls init if not defined
def getDPW(zone)
{
	def i = zone.toInteger()
	if(state.dpwMap) return state.dpwMap.get(i-1)
	return initDPW(i)
}

//Initialize Time per Week
def initTPW(i){   
    
    if("${settings["zone${i}"]}" == null || nozzle(i) == 0 || nozzle(i) == 4 || plant(i) == 0 || !zoneActive(i.toString()) ) return 0
    
    // apply gain adjustment
    def gainAdjust = 100
    if (gain && gain != 0) gainAdjust += gain    
    
    // apply seasonal adjustment is enabled and not set to new plants
    def seasonAdjust = 100
    if (state.weekseasonAdj && isSeason && settings["plant${i}"] != "New Plants") seasonAdjust = state.weekseasonAdj    
	
    def zone = i.toInteger()
	def tpw = 0
	
    // Use learned, previous tpw if it is available
	if(state.tpwMap) tpw = state.tpwMap.get(zone-1)
	// set user time with season adjust	
    if(settings["minWeek${i}"] != null && settings["minWeek${i}"] != 0) tpw = Math.round(("${settings["minWeek${i}"]}").toInteger() * seasonAdjust / 100)
    
	// initial tpw calculation
    if (tpw == null || tpw == 0 || settings["minWeek${i}"] == 0) tpw = Math.round(plant(i) * nozzle(i) * gainAdjust / 100 * seasonAdjust / 100)
    // apply gain to all zones --obsolete with learn implementation--
    //else if (gainAdjust != 100) twp = Math.round(tpw * gainAdjust / 100)
    
    //if (tpw <= 3) tpw = 3
    return tpw
}

// Get the current time per week, calls init if not defined
def getTPW(zone)
{
	def i = zone.toInteger()
	if(state.tpwMap) return state.tpwMap.get(i-1)
	return initTPW(i)
}

// Calculate daily run time based on tpw and dpw
def calcRunTime(tpw, dpw)
{           
    def duration = 0
    if(tpw > 0 && dpw > 0) {
        duration = Math.round(tpw / dpw)
    }
    return duration
}

// Check the moisture level of a zone returning dry (1) or wet (0) and adjust tpw if overly dry/wet
def moisture(i)
{
	// No Sensor on this zone or manual start skips moisture check
	if(settings["sensor${i}"] == null || i == 0) {     
        return [1,""]
    }

    // Check if sensor is reporting 6-> 12 hours -> 48 hours   
    def sixHours = new Date(now() - (1000 * 60 * 60 * 48).toLong())    
    def eventsSinceYesterday = (settings["sensor${i}"].eventsSince(sixHours, [max: 50])?.findAll { it.name == "humidity" })
    //log.debug "there have been ${eventsSinceYesterday.size()} since yesterday"
    if (eventsSinceYesterday.size() < 1){    
    	//change to seperate warning note?
        //note("warning", "Please check ${settings["sensor${i}"]}, no activity in the last 12 hours", "w")
        return [1, "Please check ${settings["sensor${i}"]}, no activity in the last 12 hours\n"]	//change to 1
    }
    
    def latestHum = settings["sensor${i}"].latestValue("humidity")
    def spHum = getDrySp(i).toInteger()
    //def moistureList = []
    if (!learn) 
    {
        // no learn mode, only looks at target moisture level
		if(latestHum <= spHum) {
           //dry soil
           return [1,"${settings["name${i}"]}, Watering ${settings["sensor${i}"]} reads ${latestHum}%, SP is ${spHum}%\n"]              
        } else {
           //wet soil
           return [0,"${settings["name${i}"]}, Skipping ${settings["sensor${i}"]} reads ${latestHum}%, SP is ${spHum}%\n"]           
        }
    }

    //learn mode
    def tpw = getTPW(i)    
    
    //change to daycount
    //def tpwAdjust = Math.round((spHum - latestHum) / getDPW(i))
    def daycount = 1
    if (state.daycount[i-1] > 0) daycount = state.daycount[i-1]    
    def tpwAdjust = 0
    if (spHum != latestHum) tpwAdjust = Math.round((spHum - latestHum) / daycount)    
    
    //adjust for rain
    
    //limit to 3 minute increments
    if (tpwAdjust <= -3) tpwAdjust = -3
    else if (tpwAdjust >= 3) tpwAdjust = 3
    log.debug "moisture adjust: ${tpwAdjust}"
    def moistureSum = ""
    
    if (tpwAdjust > 0 || (state.daycount[i-1] > (6 / getDPW(i)) ) ){
    	//only adjust if ok to run
    	def newTPW = Math.round(tpw + tpwAdjust)
    	if (newTPW <= 3) note("warning", "Please check ${settings["sensor${i}"]}, Zone ${i} time per week is very low: ${newTPW} mins/week","w")
    	if (newTPW >= 315) note("warning", "Please check ${settings["sensor${i}"]}, Zone ${i} time per week is very high: ${newTPW} mins/week","w")
    	state.tpwMap.putAt(i-1, newTPW)
        state.dpwMap.putAt(i-1, initDPW(i))
    	if (state.daycount[i-1] > (6 / getDPW(i)) ) moistureSum = "Water Zone ${i}: ${state.daycount[i-1]} days since last water, ${settings["sensor${i}"]} moisture is: ${latestHum}%, setting is ${spHum}%\n"
        else moistureSum = "Water Zone ${i}: ${settings["sensor${i}"]} moisture is: ${latestHum}%, setting is ${spHum}% time adjusted by ${tpwAdjust} mins to ${newTPW} mins/week\n"
    	return [1, "${moistureSum}"]
    }    
    else {
    	//moistureSum = "Zone ${i}: ${settings["sensor${i}"]} moisture is: ${latestHum}%, setting is ${spHum}% no time adjustment\n"
        moistureSum = "${settings["name${i}"]}, Skipping ${settings["sensor${i}"]} reads ${latestHum}%, SP is ${spHum}%\n"
        return [0, "${moistureSum}"]
    }
    //note("moisture", "${moistureSum}","m")
    return [0, "${moistureSum}"]
}  
 
//notifications to device, pushed if requested
def note(status, message, type){
	log.debug "${status}:  ${message}"
    switches.notify("${status}", "${message}")
    if(notify)
    {
      if (notify.contains('Daily') && type == "d"){       
        send "${message}"
      }
      if (notify.contains('Weather') && type == "f"){     
        send "${message}"
      }
      if (notify.contains('Warnings') && type == "w"){     
        send "${message}"
      }
      if (notify.contains('Moisture') && type == "m"){        
        send "${message}"
      }      
    }
}

//days available
def daysAvailable(){
    int dayCount = 0
    if("${settings["days"]}" == "null") dayCount = 7
    else if(days){    
	    if (days.contains('Even') || days.contains('Odd')) {
          dayCount = 4
          if(days.contains('Even') && days.contains('Odd')) dayCount = 7
        } else {
        	if (days.contains('Monday')) dayCount += 1
        	if (days.contains('Tuesday')) dayCount += 1
        	if (days.contains('Wednesday')) dayCount += 1
        	if (days.contains('Thursday')) dayCount += 1
        	if (days.contains('Friday')) dayCount += 1
        	if (days.contains('Saturday')) dayCount += 1
        	if (days.contains('Sunday')) dayCount += 1
           }
       }
    return dayCount
}    
 
//get moisture SP
def getDrySp(i){
    if ("${settings["sensorSp${i}"]}" != "null") return "${settings["sensorSp${i}"]}"  
    else if (settings["plant${i}"] == "New Plants") return 40
    else{
        switch (settings["option${i}"]) {
            case "Sand":
                return 22
            case "Clay":
                return 38  
            default:
                return 28
        }
    }
}    
     
//zone: ['Off', 'Spray', 'rotor', 'Drip', 'Master Valve', 'Pump']
def nozzle(i){
    def getT = settings["zone${i}"]    
    if (!getT) return 0
    switch(getT) {        
        case "Spray":
            return 1
        case "Rotor":
            return 1.4
        case "Drip":
            return 2.4
        case "Master Valve":
            return 4
        case "Pump":
            return 4
        default:
            return 0
    }
}
 
//plant: ['Lawn', 'Garden', 'Flowers', 'Shrubs', 'Trees', 'Xeriscape', 'New Plants']
def plant(i){
    def getP = settings["plant${i}"]    
    if(!getP) return 0
    switch(getP) {
        case "Lawn":
            return 60
        case "Garden":
            return 50
        case "Flowers":
            return 40
        case "Shrubs":
            return 30
        case "Trees":
            return 20
        case "Xeriscape":
            return 30
        case "New Plants":
            return 80
        default:
            return 0
    }
}
 
//option: ['Slope', 'Sand', 'Clay', 'No Cycle', 'Cycle 2x', 'Cycle 3x']
def cycles(i){  
    def getC = settings["option${i}"]    
    if(!getC) return 2
    switch(getC) {
        case "Slope":
            return 3
        case "Sand":
            return 1
        case "Clay":
            return 2
        case "No Cycle":
            return 1
        case "Cycle 2x":
            return 2
        case "Cycle 3x":
            return 3   
        default:
            return 2
    }    
}
 
//check if day is allowed
def isDay() {    
    log.debug "day check"
    if ("${settings["days"]}" == "null") return true
     
    def today = new Date().format("EEEE", location.timeZone)    
    def daynum = new Date().format("dd", location.timeZone)
    int dayint = Integer.parseInt(daynum)
         
    log.debug "today: ${today} ${dayint}, days: ${days}"
     
    if (days.contains(today)) return true
    if (days.contains("Even") && (dayint % 2 == 0)) return true
    if (days.contains("Odd") && (dayint % 2 != 0)) return true

    return false      
}

//set season adjustment & remove season adjustment
def setSeason() {
        
        def zone = 1
        while(zone <= 16) {    		
    		if ( !learn || (settings["sensor${zone}"] == null) || state.tpwMap[zone] == 0) {
            	state.tpwMap.putAt(zone-1, 0)
                def tpw = initTPW(zone)
                //def newTPW = Math.round(tpw * tpwAdjust / 100)
                state.tpwMap.putAt(zone-1, tpw)
    			state.dpwMap.putAt(zone-1, initDPW(zone))
                log.debug "Zone ${zone}:  seasonaly adjusted by ${state.weekseasonAdj-100}% to ${tpw}"
                }
    		
            zone++
          }       
}

//check weather
def isWeather(){
    def wzipcode = "${zipString()}"   
   	log.debug "weather ${zipString()}"   
    //seasonal q factor
    def qFact = 0.7
    // Forecast rain
    Map sdata = getWeatherFeature("forecast10day", wzipcode)
    if (sdata != null) {
    	log.debug sdata.response
		if(sdata.response.containsKey('error') {
    		note("warning", "Check Zipcode setting, error:\n${sdata.response.error}" , "w")
        	return false
		}
    } else {
    	log.debug "sdata is null"
    	note("warning", "Check Zipcode setting, error: null" , "w")
    	return false
    }
    def qpf = sdata.forecast.simpleforecast.forecastday.qpf_allday.mm       
    def qpfTodayIn = 0
    if (qpf.get(0).isNumber()) qpfTodayIn = Math.round(qpf.get(0).toInteger() /25.4 * 100) /100
    log.debug "qpfTodayIn ${qpfTodayIn}"
    def qpfTomIn = 0
    if (qpf.get(1).isNumber()) qpfTomIn = Math.round(qpf.get(1).toInteger() /25.4 * 100) /100
    log.debug "qpfTomIn ${qpfTomIn}"
    // current conditions
    Map cond = getWeatherFeature("conditions", wzipcode)    
    if (cond != null) {
    	log.debug cond.response
		if(cond.response.containsKey('error') {
    		note("warning", "Can't get weather conditions, error:\n${sdata.response.error}" , "w")
        	return false
		}
    } else {
    	log.debug "cond is null"
    	note("warning", "Can't get weather conditions, error: null" , "w")
    	return false
    }
    def TRain = 0
    if (cond.current_observation.precip_today_metric.isNumber()) TRain = Math.round(cond.current_observation.precip_today_metric.toInteger() /25.4 * 100) /100
    log.debug "TRain ${TRain}"
    // reported rain
    def YRain = 0
    Map yCond = getWeatherFeature("yesterday", wzipcode)
    if (yCond == null) {
       	log.debug "Weather check (yesterday), error: yCond is null"
    	note("warning", "Weather check (yesterday), error: yCond is null" , "w")
    	yRain = 0
    } else if (yCond.response.containsKey('error') {
		log.debug "Weather check (yesterday), error:\n${yCond.response.error}"
    	note("warning", "Weather check (yesterday), error:\n${yCond.response.error}" , "w")
	}
    else if (yCond.history.dailysummary.precipi.get(0).isNumber()) YRain = yCond.history.dailysummary.precipi.get(0)
    else YRain = 0   
    if(TRain > qpfTodayIn) qpfTodayIn = TRain    
    log.debug "TRain ${TRain} qpfTodayIn ${qpfTodayIn}"
    //state.Rain = [S,M,T,W,T,F,S]
    //state.Rain = [0,0.43,3,0,0,0,0]
    
    def day = getWeekDay()    
    state.Rain.putAt(day - 1, YRain)    
    def i = 0
    def weeklyRain = 0
    while (i <= 6){
    	def factor = 0
        if ((day - i) > 0) factor = day - i
        else factor =  day + 7 - i
        def getrain = state.Rain.get(i)
    	weeklyRain += Math.round(getrain.toFloat() / factor * 100)/100
    	i++
        }
    log.debug "weeklyRain ${weeklyRain}"
    //note("season", "weeklyRain ${weeklyRain} ${state.Rain}", "d")
           
    //get highs
    def getHigh = sdata.forecast.simpleforecast.forecastday.high.fahrenheit
    def avgHigh = Math.round((getHigh.get(0).toInteger() + getHigh.get(1).toInteger() + getHigh.get(2).toInteger() + getHigh.get(3).toInteger() + getHigh.get(4).toInteger())/5)    
    
    Map citydata = getWeatherFeature("geolookup", wzipcode)
    def weatherString = "Today's weather:  ${getHigh.get(0)}F,  ${qpfTodayIn}in rain\n Tomorrow: ${getHigh.get(1)}F,  ${qpfTomIn}in rain\n Yesterday:  ${YRain}in rain "
    if (citydata != null) {
    	if (citydata.response.containsKey('error')) {
    		log.debug "(geolookup) failed, error: ${citydata.response.error}"
    	} else weatherString = "${citydata.location.city} weather\n Today: ${getHigh.get(0)}F,  ${qpfTodayIn}in rain\n Tomorrow: ${getHigh.get(1)}F,  ${qpfTomIn}in rain\n Yesterday:  ${YRain}in rain "
    } else log.debug "(geolookup) error: citydata is null"
    
    if (isSeason)
    {        
        //daily adjust
        state.seasonAdj = Math.round(getHigh.get(0).toInteger()/avgHigh *100)        
        weatherString += "\n Adjusted ${state.seasonAdj - 100}% for Today"
        
        // Apply seasonal adjustment on Monday each week or at install
        if(getWeekDay() == 1 || state.weekseasonAdj == 0) {
            
            //get humidity
            //def gethum = sdata.forecast.simpleforecast.forecastday.avehumidity
            //def humWeek = Math.round((gethum.get(0).toInteger() + gethum.get(1).toInteger() + gethum.get(2).toInteger() + gethum.get(3).toInteger() + gethum.get(4).toInteger())/5)    

            //get daylight
            Map astro = getWeatherFeature("astronomy", wzipcode)
            if (astro == null) {
            	log.debug "(astronomy) error: astro is null"
            } else if (astro.response.containsKey('error')) {
            	log.debug "Seasonal adjustment, error: ${astro.response.error}"
            } else {
            	def getsunRH = astro.moon_phase.sunrise.hour
        		def getsunRM = astro.moon_phase.sunrise.minute
            	def getsunSH = astro.moon_phase.sunset.hour
            	def getsunSM = astro.moon_phase.sunset.minute
            	def daylight = ((getsunSH.toInteger() * 60) + getsunSM.toInteger())-((getsunRH.toInteger() * 60) + getsunRM.toInteger())
				if (daylight >= 850) daylight = 850
            
            //set seasonal adjustment
            //state.weekseasonAdj = Math.round((daylight/700 * avgHigh/75) * ((1-(humWeek/100)) * avgHigh/75)*100)
            	state.weekseasonAdj = Math.round(daylight/700 * avgHigh/70 * qFact * 100)

            //apply seasonal time adjustment
            	weatherString += "\n Applying seasonal adjustment of ${state.weekseasonAdj-100}% this week"
            //note("season", "Applying seasonal adjustment of ${state.weekseasonAdj-100}% this week", "f")
            	setSeason()
            }
        }
    }
       
    note("season", weatherString , "f")
    //move day check here to allow weathercheck every day
    if (isDay() == false) return true
    
    def setrainDelay = "0.2"
    if (rainDelay) setrainDelay = rainDelay    
    if (switches.latestValue("rainsensor") == "rainsensoron"){
        note("raintoday", "is skipping watering, rain sensor is on.", "d")        
        return true
        }    
    else if (qpfTodayIn > setrainDelay.toFloat()){              
        note("raintoday", "is skipping watering, ${qpfTodayIn}in rain today.", "d")        
        return true
        }
    else if (qpfTomIn > setrainDelay.toFloat()){     
        note("raintom", "is skipping watering, ${qpfTomIn}in rain expected tomorrow.", "d")
        return true
        }
    else if (weeklyRain > setrainDelay.toFloat()){
        note("rainy", "is skipping watering, ${weeklyRain}in average rain over the past week.", "d")
        return true
        }
    return false    
}

def getDPWDays(dpw){
  if(dpw == 1)
     return state.DPWDays1
  if(dpw == 2)
     return state.DPWDays2
  if(dpw == 3)
     return state.DPWDays3
  if(dpw == 4)
     return state.DPWDays4
  if(dpw == 5)
     return state.DPWDays5
  if(dpw == 6)
     return state.DPWDays6
  if(dpw == 7)
     return state.DPWDays7
  return [0,0,0,0,0,0,0]
}

// Create a map of what days each possible DPW value will run on
// Example:  User sets allowed days to Monday Wed and Fri
// Map would look like: DPWDays1:[1,0,0,0,0,0,0] (run on Monday)
//                      DPWDays2:[1,0,0,0,1,0,0] (run on Monday and Friday)
//                      DPWDays3:[1,0,1,0,1,0,0] (run on Monday Wed and Fri)
// Everything runs on the first day possible, starting with Monday.
def createDPWMap() {
	state.DPWDays1 = []
    state.DPWDays2 = []
    state.DPWDays3 = []
    state.DPWDays4 = []
    state.DPWDays5 = []
    state.DPWDays6 = []
    state.DPWDays7 = []
	def NDAYS = 7
    // day Distance[NDAYS][NDAYS], easier to just define than calculate everytime
    def int[][] dayDistance = [[0,1,2,3,3,2,1],[1,0,1,2,3,3,2],[2,1,0,1,2,3,3],[3,2,1,0,1,2,3],[3,3,2,1,0,1,2],[2,3,3,2,1,0,1],[1,2,3,3,2,1,0]]
	def ndaysAvailable = daysAvailable() 
	def i = 0
    def int[] daysAvailable = [0,1,2,3,4,5,6]
    if(days) 
    {
      if (days.contains('Even') || days.contains('Odd')) {
      	return
      }
	  if (days.contains('Monday')) {
    	daysAvailable[i] = 0
        i++
      }
      if (days.contains('Tuesday')) {
    	daysAvailable[i] = 1
        i++
      }
      if (days.contains('Wednesday')) {
    	daysAvailable[i] = 2
        i++
      }
      if (days.contains('Thursday')) {
    	daysAvailable[i] = 3
        i++
      }
      if (days.contains('Friday')) {
    	daysAvailable[i] = 4
        i++
      }
      if (days.contains('Saturday')) {
    	daysAvailable[i] = 5
        i++
      }
      if (days.contains('Sunday')) {
    	daysAvailable[i] = 6
        i++
      }
    
      if(i != ndaysAvailable) {
    	log.debug "ERROR: days and daysAvailable do not match."
        log.debug "${i}  ${ndaysAvailable}"
      }
    }
    //log.debug "Ndays: ${ndaysAvailable} Available Days: ${daysAvailable}"
    def maxday = -1
    def max = -1
    def days = new int[7]
    def int[][] runDays = [[0,0,0,0,0,0,0],[0,0,0,0,0,0,0],[0,0,0,0,0,0,0],[0,0,0,0,0,0,0],[0,0,0,0,0,0,0],[0,0,0,0,0,0,0],[0,0,0,0,0,0,0]]
    for(def a=0; a < ndaysAvailable; a++) {

      // Figure out next day using the dayDistance map, getting the farthest away day (max value)
      if(a > 0 && ndaysAvailable >= 2 && a != ndaysAvailable-1) {
        if(a == 1) {
		  for(def c=1; c < ndaysAvailable; c++) {
	        def d = dayDistance[daysAvailable[0]][daysAvailable[c]]
	  		if(d > max) {
	    	  max = d
	    	  maxday = daysAvailable[c]
	        }
	      }
	      //log.debug "max: ${max}  maxday: ${maxday}"
	      days[0] = maxday
        }
 
        // Find successive maxes for the following days
        if(a > 1) {
	      def lmax = max
          def lmaxday = maxday
	      max = -1
	      for(def c = 1; c < ndaysAvailable; c++) {
	        def d = dayDistance[daysAvailable[0]][daysAvailable[c]]
            def t = d > max
            if(a % 2 == 0)
            	t = d >= max
	        if(d < lmax && d >= max) {
              if(d == max) {
              	d = dayDistance[lmaxday][daysAvailable[c]]
                if(d > dayDistance[lmaxday][maxday]) {
	              max = d
	              maxday = daysAvailable[c]
                }
              } else {
	            max = d
	            maxday = daysAvailable[c]
              }
	        }
	      }
          lmax = 5
	      while(max == -1) {
            lmax = lmax -1
	        for(def c = 1; c < ndaysAvailable; c++) {
	          def d = dayDistance[daysAvailable[0]][daysAvailable[c]]
	          if(d < lmax && d >= max) {
                if(d == max) {
              	  d = dayDistance[lmaxday][daysAvailable[c]]
                  if(d > dayDistance[lmaxday][maxday]) {
	                max = d
	                maxday = daysAvailable[c]
                  }
                } else {
	              max = d
	              maxday = daysAvailable[c]
                }
	          }
	        }
            for(def d=0; d< a-2; d++)
              if(maxday == days[d])
                max = -1
	      }
          //log.debug"max: ${max}  maxday: ${maxday}"
	      days[a-1] = maxday
        }
      }
      
      // Set the runDays map using the calculated maxdays
      for(def b=0; b < 7; b++) 
      {
        // Runs every day available
        if(a == ndaysAvailable-1) {
	      runDays[a][b] = 0
	      for(def c=0; c < ndaysAvailable; c++)
	        if(b == daysAvailable[c])
	          runDays[a][b] = 1

        } else
	      // runs weekly, use first available day
	      if(a == 0)
	        if(b == daysAvailable[0])
	          runDays[a][b] = 1
	        else
	          runDays[a][b] = 0
	      else {
	        // Otherwise, start with first available day
	        if(b == daysAvailable[0])
	          runDays[a][b] = 1
	        else {
	          runDays[a][b] = 0
	          for(def c=0; c < a; c++)
	          if(b == days[c])
		        runDays[a][b] = 1
	        }
	      }
      }

    }
  
  	//log.debug "DPW: ${runDays}"
    state.DPWDays1 = runDays[0]
    state.DPWDays2 = runDays[1]
    state.DPWDays3 = runDays[2]
    state.DPWDays4 = runDays[3]
    state.DPWDays5 = runDays[4]
    state.DPWDays6 = runDays[5]
    state.DPWDays7 = runDays[6]
}

//transition page to populate app state - this is a fix for WP param
def zoneSetPage1(){
	state.app = 1
    zoneSetPage()
    }
def zoneSetPage2(){
	state.app = 2
    zoneSetPage()
    }
def zoneSetPage3(){
	state.app = 3
    zoneSetPage()
    }
def zoneSetPage4(){
	state.app = 4
    zoneSetPage()
    }
def zoneSetPage5(){
	state.app = 5
    zoneSetPage()
    }
def zoneSetPage6(){
	state.app = 6
    zoneSetPage()
    }
def zoneSetPage7(){
	state.app = 7
    zoneSetPage()
    }
def zoneSetPage8(){
	state.app = 8
    zoneSetPage()
    }
def zoneSetPage9(i){
	state.app = 9
    zoneSetPage()
    }
def zoneSetPage10(){
	state.app = 10
    zoneSetPage()
    }
def zoneSetPage11(){
	state.app = 11
    zoneSetPage()
    }
def zoneSetPage12(){
	state.app = 12
    zoneSetPage()
    }
def zoneSetPage13(){
	state.app = 13
    zoneSetPage()
    }
def zoneSetPage14(){
	state.app = 14
    zoneSetPage()
    }
def zoneSetPage15(){
	state.app = 15
    zoneSetPage()
    }
def zoneSetPage16(){
	state.app = 16
    zoneSetPage()
    }
    
def send(msg) {		
	if (location.contactBookEnabled && recipients) {		
    	log.info ( "Sending '${msg}' to selected contacts..." )       		
    	sendNotificationToContacts(msg, recipients, [event: true])		
	} else {		
    	log.info ( "Sending Push Notification '${msg}'..." ) 		
    	sendPush( msg )		
    } 		
}
