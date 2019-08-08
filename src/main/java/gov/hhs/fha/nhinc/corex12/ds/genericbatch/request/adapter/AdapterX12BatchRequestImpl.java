/*
 * Copyright (c) 2009-2018, United States Government, as represented by the Secretary of Health and Human Services.
 * All rights reserved.
 * 
 * Redistribution and use in source and binary forms, with or without
 * modification, are permitted provided that the following conditions are met:
 *     * Redistributions of source code must retain the above
 *       copyright notice, this list of conditions and the following disclaimer.
 *     * Redistributions in binary form must reproduce the above copyright
 *       notice, this list of conditions and the following disclaimer in the documentation
 *       and/or other materials provided with the distribution.
 *     * Neither the name of the United States Government nor the
 *       names of its contributors may be used to endorse or promote products
 *       derived from this software without specific prior written permission.
 * 
 * THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS "AS IS" AND
 * ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE IMPLIED
 * WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE ARE
 * DISCLAIMED. IN NO EVENT SHALL THE UNITED STATES GOVERNMENT BE LIABLE FOR ANY
 * DIRECT, INDIRECT, INCIDENTAL, SPECIAL, EXEMPLARY, OR CONSEQUENTIAL DAMAGES
 * (INCLUDING, BUT NOT LIMITED TO, PROCUREMENT OF SUBSTITUTE GOODS OR SERVICES;
 * LOSS OF USE, DATA, OR PROFITS; OR BUSINESS INTERRUPTION) HOWEVER CAUSED AND
 * ON ANY THEORY OF LIABILITY, WHETHER IN CONTRACT, STRICT LIABILITY, OR TORT
 * (INCLUDING NEGLIGENCE OR OTHERWISE) ARISING IN ANY WAY OUT OF THE USE OF THIS
 * SOFTWARE, EVEN IF ADVISED OF THE POSSIBILITY OF SUCH DAMAGE.
 */
package gov.hhs.fha.nhinc.corex12.ds.genericbatch.request.adapter;

import gov.hhs.fha.nhinc.common.nhinccommonadapter.AdapterBatchSubmissionRequestType;
import gov.hhs.fha.nhinc.common.nhinccommonadapter.AdapterBatchSubmissionResponseSecuredType;
import gov.hhs.fha.nhinc.common.nhinccommonadapter.AdapterBatchSubmissionResponseType;
import gov.hhs.fha.nhinc.common.nhinccommonadapter.AdapterBatchSubmissionSecuredRequestType;
import gov.hhs.fha.nhinc.common.nhinccommon.AssertionType;
import gov.hhs.fha.nhinc.common.nhinccommon.HomeCommunityType;
import gov.hhs.fha.nhinc.common.nhinccommon.NhinTargetCommunitiesType;
import gov.hhs.fha.nhinc.common.nhinccommon.NhinTargetCommunityType;
import gov.hhs.fha.nhinc.common.nhinccommon.NhinTargetSystemType;
import gov.hhs.fha.nhinc.corex12.ds.genericbatch.request.nhin.proxy.NhinX12BatchRequestProxyWSSecuredImpl;
import gov.hhs.fha.nhinc.messaging.server.BaseService;
import gov.hhs.fha.nhinc.nhinclib.NhincConstants;
import gov.hhs.fha.nhinc.corex12.ds.genericbatch.common.adapter.AdapterX12BatchOrchImpl;
import org.caqh.soap.wsdl.corerule2_2_0.COREEnvelopeBatchSubmission;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.TimeZone;
import java.util.UUID;

import javax.xml.ws.WebServiceContext;

/**
 *
 * @author svalluripalli, cmay
 */
public class AdapterX12BatchRequestImpl extends BaseService {

    /**
     *
     * @param body
     * @param context
     * @return AdapterBatchSubmissionResponseSecuredType
     */
    public AdapterBatchSubmissionResponseSecuredType batchSubmitTransaction(
        AdapterBatchSubmissionSecuredRequestType body, WebServiceContext context) {

    	 AdapterBatchSubmissionResponseSecuredType response = new AdapterBatchSubmissionResponseSecuredType();
         response.setCOREEnvelopeBatchSubmissionResponse(new AdapterX12BatchOrchImpl()
             .batchSubmitTransaction(body.getCOREEnvelopeBatchSubmission(), getAssertion(context)));

         return response;
    }

    /**
     *
     * @param body
     * @param context
     * @return AdapterBatchSubmissionResponseType
     */
    public AdapterBatchSubmissionResponseType batchSubmitTransaction(AdapterBatchSubmissionRequestType body,
        WebServiceContext context) {

        AdapterBatchSubmissionResponseType response = new AdapterBatchSubmissionResponseType();
        
 COREEnvelopeBatchSubmission req = body.getCOREEnvelopeBatchSubmission();
        
        NhinTargetSystemType targetSystem = new NhinTargetSystemType();
        NhinTargetCommunitiesType ntct = new NhinTargetCommunitiesType();
        NhinTargetCommunityType tc = new NhinTargetCommunityType();
        AssertionType assertion = getAssertion(context, body.getAssertion());
        HomeCommunityType hc = assertion.getHomeCommunity();
        TimeZone tz = TimeZone.getTimeZone("US/Eastern");
        DateFormat df = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSS"); // Quoted "Z" to indicate UTC, no timezone offset
        df.setTimeZone(tz);
        String nowAsISO = df.format(new Date());
        
        req.setTimeStamp(nowAsISO);
        req.setPayloadID(UUID.randomUUID().toString());
        targetSystem.setHomeCommunity(hc);
        NhincConstants.GATEWAY_API_LEVEL level = NhincConstants.GATEWAY_API_LEVEL.LEVEL_g1;
       // System.out.println("payload is "+req.)
        response.setCOREEnvelopeBatchSubmissionResponse(new NhinX12BatchRequestProxyWSSecuredImpl()
            .batchSubmitTransaction(req,assertion,targetSystem,level));

        return response;
    }
}
