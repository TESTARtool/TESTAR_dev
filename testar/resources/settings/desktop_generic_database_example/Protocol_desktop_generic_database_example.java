/***************************************************************************************************
 *
 * Copyright (c) 2019 Open Universiteit - www.ou.nl
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


import java.sql.*;
import org.fruit.alayer.*;
import org.testar.protocols.DesktopProtocol;

/**
 * This protocol provides an example how to use a SQL database connection to get specific input for example for login
 */
public class Protocol_desktop_generic_database_example extends DesktopProtocol {



	/**
	 * This method is invoked each time the TESTAR starts the SUT to generate a new sequence.
	 * This can be used for example for bypassing a login screen by filling the username and password
	 * or bringing the system into a specific start state which is identical on each start (e.g. one has to delete or restore
	 * the SUT's configuration files etc.)
	 */
	 @Override
	protected void beginSequence(SUT system, State state){
	 	// this is an example how to use database connection to Microsoft SQL to get input:

		 // Create a variable for the connection string.
		 String connectionUrl = "jdbc:sqlserver://addYourSqlServerAddressHere:addYourSqlServerPortHere;databaseName=addYourDatabaseNameHere;user=addYourUsernameHere;password=addYourPasswordHere";

		 try (Connection con = DriverManager.getConnection(connectionUrl); Statement stmt = con.createStatement();) {
			 String SQL = "SELECT TOP 10 * FROM Person.Contact";
			 ResultSet rs = stmt.executeQuery(SQL);

			 // Iterate through the data in the result set and display it.
			 while (rs.next()) {
				 System.out.println(rs.getString("FirstName") + " " + rs.getString("LastName"));
			 }
		 }
		 // Handle any errors that may have occurred.
		 catch (SQLException e) {
			 e.printStackTrace();
		 }

	 	super.beginSequence(system, state);
	}


}
