#pragma once
#include <vector>
#include "Types.h"

/*-----------------
	FileUtils
------------------*/

class FileUtils
{
public:
	static Vector<BYTE>		ReadFile(const char* path);
	static WString			Convert(string str);
};