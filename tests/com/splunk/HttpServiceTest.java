/*
 * Copyright 2012 Splunk, Inc.
 *
 * Licensed under the Apache License, Version 2.0 (the "License"): you may
 * not use this file except in compliance with the License. You may obtain
 * a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS, WITHOUT
 * WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. See the
 * License for the specific language governing permissions and limitations
 * under the License.
 */

package com.splunk;

import org.junit.Before;
import org.junit.Test;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.PrintStream;
import java.io.UnsupportedEncodingException;
import java.util.HashMap;

public class HttpServiceTest extends SDKTestCase {
    private HttpService httpService;
    
    @Before
    @Override
    public void setUp() throws Exception {
        super.setUp();
        
        httpService = new HttpService(
                (String)command.opts.get("host"),
                (Integer)command.opts.get("port"),
                (String)command.opts.get("scheme")
        );
    }

    private boolean firstLineIsXmlDtd(InputStream stream) {
        InputStreamReader reader;
        try {
            reader = new InputStreamReader(stream, "UTF8");
        } catch (UnsupportedEncodingException e) {
            throw new Error(e);
        }
        BufferedReader lineReader = new BufferedReader(reader);
        try {
            return "<?xml version=\"1.0\" encoding=\"UTF-8\"?>".equals(
                    lineReader.readLine()
            );
        } catch (IOException e) {
            fail(e.toString());
            return false;
        }
    }

    @Test
    public void testGet() {
        ResponseMessage response = httpService.get("/");
        assertEquals(200, response.getStatus());
        assertTrue(firstLineIsXmlDtd(response.getContent()));
    }

    @Test
    public void testPost() {
        HashMap<String, Object> args = new HashMap<String, Object>();
        args.put("foo", "bar");
        ResponseMessage response = httpService.post("/", args);
        assertEquals(200, response.getStatus());
        assertTrue(firstLineIsXmlDtd(response.getContent()));
    }

    @Test
    public void testSend() {
        RequestMessage request = new RequestMessage("GET");
        ResponseMessage response = service.send("/", request);
        assertEquals(200, response.getStatus());
        assertTrue(firstLineIsXmlDtd(response.getContent()));
    }

    @Test
    public void testRequestMessage() {
        RequestMessage request = new RequestMessage("GET");
        assertTrue(request.checkMethod(request.getMethod()));
        request.setMethod("POST");
        assertTrue(request.checkMethod(request.getMethod()));
        assertEquals(request.getMethod(), "POST");
        
        ByteArrayOutputStream stream = new ByteArrayOutputStream();
        try {
            stream.write("HELLO".getBytes("UTF-8"));
        } catch (Exception e) {
            fail("Exception!");
        }
        
        assertNull(request.getContent());
        request.setContent(stream);
        assertNotNull(request.getContent());
    }

    @Test
    public void testResponseMessage() {
        ResponseMessage response = new ResponseMessage(200);
        assertEquals(response.getStatus(), 200);
        assertTrue(response.getHeader() != null);
    }

    @Test
    public void testDelete() {
        Args deleteArgs = Args.create("output_mode", "json");
        try {
            httpService.delete("/search/jobs/foobar_doesntexist", deleteArgs);
        } catch (HttpException e) {
            assertEquals(404, e.getStatus());
            assertNotNull(e.getDetail());
        }
    }
}
