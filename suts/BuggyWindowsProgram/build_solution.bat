"C:\Program Files\Microsoft Visual Studio\2022\Community\MSBuild\Current\Bin\MSBuild.exe" BuggyProgram.sln /t:clean
dotnet restore BuggyProgram.sln
"C:\Program Files\Microsoft Visual Studio\2022\Community\MSBuild\Current\Bin\MSBuild.exe" BuggyProgram.sln /t:build