/**
 * Copyright (C) 2013 Aurélien Chabot <aurelien></aurelien>@chabot.fr>
 *
 * This file is part of DroidUPNP.
 *
 * DroidUPNP is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * DroidUPNP is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with DroidUPNP.  If not, see <http:></http:>//www.gnu.org/licenses/>.
 */
package tech.pixelw.cling_common.entity

import android.util.Log
import org.fourthline.cling.model.meta.Device
import org.fourthline.cling.model.types.UDAServiceType

data class CDevice(var device: Device<*, *, *>) : IUpnpDevice {
    override fun mDevice(): Device<*, *, *> {
        return device
    }

    override val displayString: String
        get() = device.displayString

    override val friendlyName: String
        get() = if (device.details != null && device.details.friendlyName != null) device.details
            .friendlyName else displayString

    override fun equals(other: Any?): Boolean {
        return other?.let {
            if (it is CDevice){
                device.identity.udn == it.device.identity.udn
            } else null
        } == true
    }

    override val uID: String
        get() = device.identity.udn.toString()

    override val extendedInformation: String
        get() {
            var info = ""
            if (device.findServiceTypes() != null) for (cap in device.findServiceTypes()) {
                info += """
	${cap.type} : ${cap.toFriendlyString()}"""
            }
            return info
        }

    override fun printService() {
        val findServices = device.findServices()
        for (service in findServices) {
            Log.i(TAG, "\t Service : $service")
            for (a in service.actions) {
                Log.i(TAG, "\t\t Action : $a")
            }
        }
    }

    override fun asService(service: String): Boolean {
        return device.findService(UDAServiceType(service)) != null
    }

    override fun hashCode(): Int {
        return device.identity.udn.hashCode()
    }

    override val manufacturer: String
        get() = device.details.manufacturerDetails.manufacturer

    override val manufacturerURL: String
        get() = try {
            device.details.manufacturerDetails.manufacturerURI.toString()
        } catch (e: Exception) {
            ""
        }

    override val modelName: String
        get() = try {
            device.details.modelDetails.modelName
        } catch (e: Exception) {
            ""
        }

    override val modelDesc: String
        get() = try {
            device.details.modelDetails.modelDescription
        } catch (e: Exception) {
            ""
        }

    override val modelNumber: String
        get() = try {
            device.details.modelDetails.modelNumber
        } catch (e: Exception) {
            ""
        }

    override val modelURL: String
        get() = try {
            device.details.modelDetails.modelURI.toString()
        } catch (e: Exception) {
            ""
        }

    override val xMLURL: String
        get() = try {
            device.details.baseURL.toString()
        } catch (e: Exception) {
            ""
        }
    override val presentationURL: String
        get() = try {
            device.details.presentationURI.toString()
        } catch (e: Exception) {
            ""
        }

    override val serialNumber: String
        get() = try {
            device.details.serialNumber
        } catch (e: Exception) {
            ""
        }

    override val uDN: String
        get() = try {
            device.identity.udn.toString()
        } catch (e: Exception) {
            ""
        }

    override val isFullyHydrated: Boolean
        get() = device.isFullyHydrated

    companion object {
        private const val TAG = "ClingDevice"
    }

}