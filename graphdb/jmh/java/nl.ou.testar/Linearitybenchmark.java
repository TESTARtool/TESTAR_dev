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

import org.openjdk.jmh.annotations.*;

import java.util.concurrent.TimeUnit;

/**
 * Benchmark to test the linear behaviour of adding states to the database.
 * A Set of states will be stored in the database. In each iteration of the benchmark the set
 * of states is doubled. If the behaviour is linear, the amount of time required to execute the
 * benchmark will also change linear.
 */
@State(Scope.Benchmark)
public class Linearitybenchmark {

    OrientDBRepository graphFactory;

    @Setup(Level.Invocation)
    public void setupDatabase() {
        graphFactory = new OrientDBRepository("plocal:/tmp/benchmark" +
                "benchmark","admin","admin");
       graphFactory.addState(Util.createState("tagI"+0),true);
    }

    @TearDown(Level.Invocation)
    public void dropDatabase() {
        graphFactory.dropDatabase();
    }

    @Param({"1","2","4","8","16","32","64","128","256","512","1024"})
    private int interations;

    @Benchmark()
    @Warmup(iterations = 30)
    @Fork(10)
    @Measurement(iterations = 10, time=1, timeUnit = TimeUnit.MILLISECONDS)
    @BenchmarkMode({Mode.SampleTime})
    @OutputTimeUnit(TimeUnit.MILLISECONDS)
    public void testAddStateLiniairity() {
        for (int i = 1; i<interations; i++)
        graphFactory.addState(Util.createState("tagI"+i),false);
    }
}
