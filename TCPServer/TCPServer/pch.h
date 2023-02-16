#pragma once

//#pragma comment(lib, "\\x64\\Debug\\libServerCore.a")

#define PY_SSIZE_T_CLEAN
#include <python3.6m/Python.h>

#include <../Libraries/Libs/x64/Debug/libServerCore.a>
#include "CorePch.h"

using DetectingSessionRef = shared_ptr<class DetectingSession>;
using UserRef = shared_ptr<class User>;