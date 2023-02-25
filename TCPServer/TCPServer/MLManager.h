#pragma once

#define PY_SSIZE_T_CLEAN
#include <Python.h>

class MLManager
{
public:
	MLManager();
	~MLManager();

	void Init();
	int8 RunModel(const float features[], uint16 featureCount);

public:
	USE_LOCK;

	string _MLFileName = "server.server_main";
	string _modelPath = "/usr/projects/Object_Detection/save/svm_model.pkl";
	uint8 _numOfCall = 1;
	vector<string> _MLFunctionName;

	PyObject* _pName, * _pModule, * _pFunc, * _pModelPath;
};

