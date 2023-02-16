#pragma once

/*---------------
	ConsoleLog
----------------*/

typedef uint8 StdFd;

enum class Color
{
	DEFAULT,
	BLACK,
	WHITE,
	RED,
	GREEN,
	BLUE,
	YELLOW,
};

class ConsoleLog
{
	enum { BUFFER_SIZE = 4096 };

public:
	ConsoleLog();
	~ConsoleLog();

public:
	void		WriteStdOut(Color color, const char* str, ...);
	void		WriteStdErr(Color color, const char* str, ...);

protected:
	void		SetConsoleAttribute(StdFd stdFd, const char* attributes);
	void		SetColor(bool stdOut, Color color);

private:
	StdFd		_stdOut = STDIN_FILENO;
	StdFd		_stdErr = STDERR_FILENO;
	const char* attribute;
};