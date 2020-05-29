/***************************************************************************************************
 *
 * Copyright (c) 2019, 2020 Universitat Politecnica de Valencia - www.upv.es
 * Copyright (c) 2019, 2020 Open Universiteit - www.ou.nl
 *
 * Redistribution and use in source and binary forms, with or without
 * modification, are permitted provided that the following conditions are met:
 *
 * 1. Redistributions of source code must retain the above copyright notice,
 * this list of conditions and the following disclaimer.
 * 2. Redistributions in binary form must reproduce the above copyright
 * notice, this list of conditions and the following disclaimer in the
 * documentation and/or other materials provided with the distribution.
 * 3. Neither the name of the copyright holder nor the names of its
 * contributors may be used to endorse or promote products derived from
 * this software without specific prior written permission.
 *
 * THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS "AS IS"
 * AND ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE
 * IMPLIED WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE
 * ARE DISCLAIMED. IN NO EVENT SHALL THE COPYRIGHT OWNER OR CONTRIBUTORS BE
 * LIABLE FOR ANY DIRECT, INDIRECT, INCIDENTAL, SPECIAL, EXEMPLARY, OR
 * CONSEQUENTIAL DAMAGES (INCLUDING, BUT NOT LIMITED TO, PROCUREMENT OF
 * SUBSTITUTE GOODS OR SERVICES; LOSS OF USE, DATA, OR PROFITS; OR BUSINESS
 * INTERRUPTION) HOWEVER CAUSED AND ON ANY THEORY OF LIABILITY, WHETHER IN
 * CONTRACT, STRICT LIABILITY, OR TORT (INCLUDING NEGLIGENCE OR OTHERWISE)
 * ARISING IN ANY WAY OUT OF THE USE OF THIS SOFTWARE, EVEN IF ADVISED OF THE
 * POSSIBILITY OF SUCH DAMAGE.
 *******************************************************************************************************/

package org.testar.json.object;

import org.fruit.monkey.ConfigTags;
import org.fruit.monkey.Settings;

import com.fasterxml.jackson.annotation.JsonCreator;

public class SettingsJsonObject{

	String sutConnector;
	String sutConnectorValue;
	String suspiciousTitles;
	String clickFilter;
	String protocolClass;
	boolean forceForeground;
	double actionDuration;
	double timeToWaitAfterAction;
	double timeToFreeze;
	double maxReward;
	double discount;

	@JsonCreator
	public SettingsJsonObject(Settings settings) {
		this.sutConnector = settings.get(ConfigTags.SUTConnector);
		this.sutConnectorValue = settings.get(ConfigTags.SUTConnectorValue);
		this.suspiciousTitles = settings.get(ConfigTags.SuspiciousTitles);
		this.clickFilter = settings.get(ConfigTags.ClickFilter);
		this.protocolClass = settings.get(ConfigTags.ProtocolClass);;
		this.forceForeground = settings.get(ConfigTags.ForceForeground);
		this.actionDuration = settings.get(ConfigTags.ActionDuration);
		this.timeToWaitAfterAction = settings.get(ConfigTags.TimeToWaitAfterAction);
		this.timeToFreeze = settings.get(ConfigTags.TimeToFreeze);
		this.maxReward = settings.get(ConfigTags.MaxReward);
		this.discount = settings.get(ConfigTags.Discount);
	}

}