#pragma once
#include "LockQueue.h"

/*------------------------------
	Macro for memory align
------------------------------*/
#pragma region Macro for memory align
#if defined(_MSC_VER)
#define ALIGNED_(x) __declspec(align(x))
#else
#if defined(__GNUC__)
#define ALIGNED_(x) __attribute__ ((aligned(x)))
#endif
#endif

#define _ALIGNED_TYPE(t, x) typedef t ALIGNED_(x)
#pragma endregion

enum
{
	SLIST_ALIGNMENT = 16
};

/*-----------------
	MemoryHeader
------------------*/

_ALIGNED_TYPE(struct, SLIST_ALIGNMENT)
MemoryHeader
{
	// [MemoryHeader][Data]
	MemoryHeader(int32 size) : allocSize(size) { }

	static void* AttachHeader(MemoryHeader* header, int32 size)
	{
		new(header)MemoryHeader(size); // placement new
		return reinterpret_cast<void*>(++header);
	}

	static MemoryHeader* DetachHeader(void* ptr)
	{
		MemoryHeader* header = reinterpret_cast<MemoryHeader*>(ptr) - 1;
		return header;
	}

	int32 allocSize;

};

/*-----------------
	MemoryPool
------------------*/

_ALIGNED_TYPE(class, SLIST_ALIGNMENT)
MemoryPool
{
public:
	MemoryPool(int32 allocSize);
	~MemoryPool();


	void			Push(MemoryHeader* ptr);
	MemoryHeader*	Pop();

private:
	LockQueue<MemoryHeader*>	_headerQueue;
	int32			_allocSize = 0;
	atomic<int32>	_useCount = 0;
	atomic<int32>	_reserveCount = 0;
};

