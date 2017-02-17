# AccProbe: The Accessibility Probe

##Overview
###64-bit

The **Accessibility Probe (AccProbe)** is a standalone, Eclipse Rich-Client Product (RCP) application that provides a view of the [Microsoft Active Accessibility (MSAA)](http://msdn2.microsoft.com/en-us/library/ms697707.aspx) or [IAccessible2](http://a11y.org/ia2) hierarchy of a currently running application or rendered document and of the properties of the accessible objects of that application or document. It can also serve as an event monitor for tracking the events fired by these accessible objects. It is meant to combine the functionality of tools like Microsoft's Inspect32, AccExplore, and AccEvent into one easy-to-use application for accessibility testing and debugging.

##Getting Started

Since **AccProbe** is an Eclipse RCP application, ease of use has been a primary objective. Simply unpack the archive you chose to download above to a directory of your choosing. A top-level ```accprobe``` directory will be created for you. Within this directory, run ```accprobe.exe``` and you're off! For more detailed instructions and using **AccProbe**, consult the *User's Guide*, also found in the ```accprobe``` directory.

**Note:** If you intend to inspect or monitor an [IAccessible2-enabled](http://a11y.org/ia2) application, you will need to register the ```IAccessible2Proxy.dll``` packaged with **AccProbe**. You do this as follows:

In order to install the IA2 Proxy, you need to run regsvr32 from the command prompt as an administrator. To do this, go to the Start Menu and search for the command prompt menu item. If you are running on Windows 7 systems, you must ```run regsvr32``` with administrator privileges. To do that, place focus on the menu item, open the item's context menu (either via right-click or F-10) and use the context menu to select "Run as Administrator", otherwise, simply activate the menu item.
```
regsvr32 dllname
```
where "dllname" is the full path to the ```IAccessible2Proxy.dll```.

For example:

```
regsvr32 C:/accprobe1.2/accprobe/plugins/org.a11y.utils.accprobe.accservice.win32.ia2_1.2.0/IAccessible2Proxy.dll.
```

##Building AccProbe

There are several steps for building **AccProbe**, depending upon whether or not you wish to build the requisite DLLs:

* Extract the source code
    * Create a workspace in your eclipse root parallel to plugins directory
    * In Eclipse, import the following projects into the workspace by File->Import->General->Existing Projects into Workspace
        * org.a11y.utils.accprobe.core
        * org.a11y.utils.accprobe.accservice
        * org.a11y.utils.accprobe.accservice.win32.ia2-fragment
        * org.a11y.utils.accprobe.accservice.win32.msaa-fragment
        * org.a11y.utils.accprobe
        * org.a11y.utils.accprobe-feature

* Open ```/org.a11y.utils.accprobe/accprobe.product``` in product configuration editor and go to ```Dependencies``` tab. Make sure ```org.eclipse.rcp``` bundle version matches with the one in your plugins directory. If not, remove ```org.eclipse.rcp``` from ```dependencies``` and click Add and type ```org.eclipse.rcp``` in feature selection dialog and select the appropriate bundle.

* You will also need the following bundles from the [Orbit project](http://www.eclipse.org/orbit) for an IDE build:
    * org.apache.commons.beanutils
    * org.apache.commons.collections
    * org.apache.commons.jxpath
    * org.apache.commons.logging

Please check your eclipse plugins directory if these bundles exist before downloading them. Download the latest versions and for ```org.apache.commons.logging```, download the one which is used for logging and *not* for http manipulation. Right now, the latest version for ```org.apache.commons.logging``` is 1.04. Download the bundles, drop them into your plugins directory, and restart the workbench.

* build the AccProbe product:
    1. Select File>Export... and choose Plug-inDevelopment>Eclipse Product
    2. Click Next
    3. Select ```/org.a11y.utils.accprobe/accprobe.product``` for the Configuration or Browse to it
    4. Type ```accprobe``` as the root directory and check the synchronize checkbox
    5. Select either an archive or directory to which to export the product and provide the remaining requisite information
    6. Click Finish
* [build the IAccessible2Proxy.dll](http://www.linux-foundation.org/en/Accessibility/IAccessible2/ComProxyDLL) (optional)
* [build the MSAA- and IAccessible2-specific dlls for AccProbe](http://accessibility.linuxfoundation.org/a11yweb/util/accprobe/buildingAccprobeDlls.html) (optional)

##License

Copyright (c) 2007, 2010, 2017 IBM Corporation
Copyright (c) 2010 Linux Foundation

All rights reserved.

Redistribution and use in source and binary forms, with or without modification, are permitted provided that the following conditions are met:

Redistributions of source code must retain the above copyright notice, this list of conditions and the following disclaimer.
Redistributions in binary form must reproduce the above copyright notice, this list of conditions and the following disclaimer in the documentation and/or other materials provided with the distribution.
Neither the name of the Linux Foundation nor the names of its contributors may be used to endorse or promote products derived from this software without specific prior written permission.

THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS "AS IS" AND ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE IMPLIED WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE ARE DISCLAIMED. IN NO EVENT SHALL THE COPYRIGHT HOLDER OR CONTRIBUTORS BE LIABLE FOR ANY DIRECT, INDIRECT, INCIDENTAL, SPECIAL, EXEMPLARY, OR CONSEQUENTIAL DAMAGES (INCLUDING, BUT NOT LIMITED TO, PROCUREMENT OF SUBSTITUTE GOODS OR SERVICES; LOSS OF USE, DATA, OR PROFITS; OR BUSINESS INTERRUPTION) HOWEVER CAUSED AND ON ANY THEORY OF LIABILITY, WHETHER IN CONTRACT, STRICT LIABILITY, OR TORT (INCLUDING NEGLIGENCE OR OTHERWISE) ARISING IN ANY WAY OUT OF THE USE OF THIS SOFTWARE, EVEN IF ADVISED OF THE POSSIBILITY OF SUCH DAMAGE.

This BSD License conforms to the Open Source Initiative's [Simplified BSD License](http://www.opensource.org/licenses/bsd-license.php) as published at:
http://www.opensource.org/licenses/bsd-license.php
