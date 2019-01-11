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
 * Benchmark to measure the time it takes to add a State to the data model.
 * There are 2 scenario's.
 * 1 Add a State once
 * 2 Add a State twice (it should be updated).
 */
@State(Scope.Benchmark)
public class AddSateBenchmark {

    OrientDBRepository graphFactory;

    @Setup(Level.Invocation)
    public void setupDatabase() {
        graphFactory = new OrientDBRepository("plocal:/tmp/benchmark" +
                "benchmark","admin","admin");
       graphFactory.addState(Util.createState("0xDEAD"),true);
    }

    @TearDown(Level.Invocation)
    public void dropDatabase() {
       graphFactory.dropDatabase();
    }

    @Benchmark()
    @Warmup(iterations = 30)
    @Fork(10)
    @Measurement(iterations = 10, time=1, timeUnit = TimeUnit.MILLISECONDS)
    @BenchmarkMode({Mode.SampleTime})
    @OutputTimeUnit(TimeUnit.MILLISECONDS)
    public void testAddState() {
        graphFactory.addState(Util.createState("0xCAFE"),true);
    }

    @Benchmark()
    @Warmup(iterations = 10)
    @Fork(10)
    @Measurement(iterations = 10, time=1, timeUnit = TimeUnit.MILLISECONDS)
    @BenchmarkMode({Mode.SampleTime})
    @OutputTimeUnit(TimeUnit.MILLISECONDS)
    public void testAddStateTwice() {
        graphFactory.addState(Util.createState("0xCAFE"),false);
        graphFactory.addState(Util.createState("0xCAFE"),false);
    }

}
