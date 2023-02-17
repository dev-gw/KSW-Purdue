#include "pch.h"
#include "MLManager.h"


MLManager::MLManager()
{
    Init();
}

MLManager::~MLManager()
{
    
}

void MLManager::Init()
{

    Py_Initialize();
    _pName = PyUnicode_DecodeFSDefault(_MLFileName.c_str());
    /* Error checking of pName left out */

    _pModule = PyImport_Import(_pName);
    Py_DECREF(_pName);

    if (_pModule == NULL)
    {
        PyErr_Print();
        fprintf(stderr, "Failed to load \"%s\"\n", _MLFileName);
        ASSERT_CRASH(true == false);
    }
    
}

WAVData MLManager::ConvertToWAV(BYTE* data)
{

}

int8 MLManager::RunModel(const WAVData& data)
{
    PyObject* pArgs, * pValue;

    ASSERT_CRASH(_pModule != NULL)
    _pFunc = PyObject_GetAttrString(_pModule, _MLFunctionName[0].c_str());
    /* pFunc is a new reference */

    if (_pFunc && PyCallable_Check(_pFunc))
    {
        pArgs = PyTuple_New(0);

        pValue = PyObject_CallObject(_pFunc, pArgs);
        Py_DECREF(pArgs);
        if (pValue != NULL)
        {
            int8 result = PyLong_AsLong(pValue);
            printf("Result of call: %d\n", result);
            Py_DECREF(pValue);
            return result;
        }
        else
        {
            Py_DECREF(_pFunc);
            Py_DECREF(_pModule);
            PyErr_Print();
            fprintf(stderr, "Call failed\n");
            ASSERT_CRASH(true == false);
        }
    }
    else
    {
        if (PyErr_Occurred())
            PyErr_Print();
        fprintf(stderr, "Cannot find function \"%s\"\n", _MLFunctionName[0]);
    }
    Py_XDECREF(_pFunc);
    Py_DECREF(_pModule);
    
    if (Py_FinalizeEx() < 0)
    {
        printf("Py_FinalizeEx() < 0 \n");
        ASSERT_CRASH(true == false);
    }
}