/*
 * Copyright 2009 Toni Menzel.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or
 * implied.
 *
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.ops4j.pax.exam.spi.container;

import java.io.IOException;
import java.io.InputStream;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import org.ops4j.pax.exam.raw.extender.ProbeInvoker;
import org.ops4j.pax.exam.spi.container.internal.ClassMethodProbeCall;
import org.ops4j.pax.exam.spi.container.internal.TestProbeBuilderImpl;

/**
 * @author Toni Menzel
 * @since Jan 11, 2010
 */
public class DefaultRaw
{

    private static int id = 0;

    public static TestProbeBuilder createProbe()
    {
        return new TestProbeBuilderImpl();
    }

    public static ProbeCall call( Class clazz, String method )
    {
        return new ClassMethodProbeCall( "PaxExam-Executable-SIG" + ( id++ ), clazz, method );
    }

    public static ProbeCall[] call( Class clazz )
    {
        List<ProbeCall> calls = new ArrayList<ProbeCall>();
        for( String m : parseMethods( clazz ) )
        {
            calls.add( new ClassMethodProbeCall( "PaxExam-Executable-SIG" + ( id++ ), clazz, m ) );
        }
        return calls.toArray( new ProbeCall[calls.size()] );
    }

    /**
     * parse test methods using reflection
     *
     * @param clazz
     * @return
     */
    private static String[] parseMethods( Class clazz )
    {
        List<String> calls = new ArrayList<String>();

        for( Method m : clazz.getDeclaredMethods() )
        {
            calls.add( m.getName() );
        }
        return calls.toArray( new String[calls.size()] );
    }

    public static void execute( TestTarget target, ProbeCall call )
        throws InvocationTargetException, ClassNotFoundException, IllegalAccessException, InstantiationException
    {
        target.getService( ProbeInvoker.class, "(Probe-Signature=" + call.signature() + ")", 0 ).call();
    }

    public static InputStream fromURL( String s )
    {
        try
        {
            return new URL( s ).openStream();
        } catch( IOException e )
        {
            throw new RuntimeException( e );
}
    }

    public static TestProbeProvider probe( final InputStream is, final String... testsSignatures )
    {
        return new TestProbeProvider()
        {

            public ProbeCall[] getTests()
            {
                List<ProbeCall> calls = new ArrayList<ProbeCall>();
                for( final String test : testsSignatures )
                {
                    calls.add( new ProbeCall()
                    {
                        public String getInstruction()
                        {
                            return null;
                        }

                        public String signature()
                        {
                            return test;
                        }
                    }
                    );
                }
                return calls.toArray( new ProbeCall[calls.size()] );
            }

            public InputStream getStream()
            {
                return is;
            }
        };
    }

}
