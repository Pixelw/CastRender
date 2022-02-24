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
package tech.pixelw.dmp_core.entity

import org.fourthline.cling.model.meta.Device


interface IUpnpDevice {
    fun mDevice(): Device<*, *, *>
    val displayString: String?
    val friendlyName: String?
    val extendedInformation: String?
    val manufacturer: String?
    val manufacturerURL: String?
    val modelName: String?
    val modelDesc: String?
    val modelNumber: String?
    val modelURL: String?
    val xMLURL: String?
    val presentationURL: String?
    val serialNumber: String?
    val uDN: String?
    override fun equals(other: Any?): Boolean
    val uID: String?
    fun asService(service: String): Boolean
    fun printService()
    val isFullyHydrated: Boolean
    override fun toString(): String
}