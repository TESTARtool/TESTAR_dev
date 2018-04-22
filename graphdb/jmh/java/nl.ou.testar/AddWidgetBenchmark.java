/***************************************************************************************************
*
* Copyright (c) 2017 Open Universiteit - www.ou.nl
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


package nl.ou.testar;

import org.fruit.alayer.StdWidget;
import org.fruit.alayer.Tags;
import org.fruit.alayer.Widget;
import org.openjdk.jmh.annotations.*;

import java.util.concurrent.TimeUnit;

/**
 * Benchmark to measure the duration of adding a single widget.
 * Add Widget to state in a database that contains only 1 state.
 *
 */
@State(Scope.Benchmark)
public class AddWidgetBenchmark {

    OrientDBRepository graphFactory;

    Widget widget = createWidget("0xDADA");
    Widget dummy  = createWidget("0xADAD");

    @Setup(Level.Invocation)
    public void setupDatabase() {
        graphFactory = new OrientDBRepository("plocal:/tmp/benchmark" +
                "/benchmark","admin","admin");

        graphFactory.addState(Util.createState("widget"+1),true);
        graphFactory.addWidget("widget1",dummy);

    }

    @TearDown(Level.Invocation)
    public void dropDatabase() {
        graphFactory.dropDatabase();
    }


    @Benchmark
    @Warmup(iterations = 30)
    @Fork(10)
    @Measurement(iterations = 10, time=1, timeUnit = TimeUnit.MILLISECONDS)
    @BenchmarkMode({Mode.SampleTime})
    @OutputTimeUnit(TimeUnit.MILLISECONDS)
    public void addSingleWidgetToState() {
        graphFactory.addWidget("widget1",widget);
    }


    private Widget createWidget(String id) {
        Widget widget = new StdWidget();
        widget.set(Tags.ConcreteID,id);
        widget.set(Tags.Abstract_R_ID,"role");
        widget.set(Tags.Abstract_R_T_ID,"role_type");
        widget.set(Tags.Abstract_R_T_P_ID,"role_type_path");
        widget.set(Tags.Desc,"Demo");
        return widget;
    }
}
