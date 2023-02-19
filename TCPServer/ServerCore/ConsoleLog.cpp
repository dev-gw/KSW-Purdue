#include "pch.h"
#include "ConsoleLog.h"

ConsoleLog::ConsoleLog()
{
}

ConsoleLog::~ConsoleLog()
{
}

void ConsoleLog::WriteStdOut(Color color, const char* format, ...)
{
	if (format == nullptr)
		return;

	SetColor(true, color);

	va_list ap;
	va_start(ap, format);
	::vprintf(format, ap);
	va_end(ap);

	fflush(stdout);

	SetColor(true, Color::WHITE);
}

void ConsoleLog::WriteStdErr(Color color, const char* format, ...)
{
	char buffer[BUFFER_SIZE];

	if (format == nullptr)
		return;

	SetColor(false, color);

	va_list ap;
	va_start(ap, format);
	::vsnprintf(buffer, BUFFER_SIZE, format, ap);
	va_end(ap);

	::fprintf(stderr, buffer);
	fflush(stderr);

	SetColor(false, Color::WHITE);
}

void ConsoleLog::SetConsoleAttribute(StdFd stdFd, const char* attributes)
{

}

void ConsoleLog::SetColor(bool stdOut, Color color)
{
	static const char* SColors[]
	{
		"\033[39m", "\033[30m", "\033[97m", "\033[31m", "\033[92m", "\033[94m", "\033[33m"
	};

	SetConsoleAttribute(stdOut ? _stdOut : _stdErr, SColors[static_cast<int32>(color)]);
}