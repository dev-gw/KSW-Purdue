#include "pch.h"
#include "FileUtils.h"
#include <filesystem>
#include <fstream>

/*-----------------
	FileUtils
------------------*/

namespace fs = std::filesystem;

Vector<BYTE> FileUtils::ReadFile(const char* path)
{
	Vector<BYTE> ret;

	fs::path filePath{ path };

	const uint32 fileSize = static_cast<uint32>(fs::file_size(filePath));
	ret.resize(fileSize);

	basic_ifstream<BYTE> inputStream{ filePath };
	inputStream.read(&ret[0], fileSize);

	return ret;
}

WString FileUtils::Convert(string str)
{
	const int32 srcLen = static_cast<int32>(str.size());

	WString ret;
	if (srcLen == 0)
		return ret;
	
	::mbtowc(nullptr, 0, 0);

	const int32 retLen = ::mbtowc(NULL, &str[0], srcLen);
	ret.resize(retLen);
	::mbtowc(&ret[0], &str[0], retLen);

	return ret;
}
