#include "pch.h"
#include "Allocator.h"
#include "Memory.h"
#include <sys/mman.h>

/*-------------------
	BaseAllocator
-------------------*/

void* BaseAllocator::Alloc(int32 size)
{
	return ::malloc(size);
}

void BaseAllocator::Release(void* ptr)
{
	::free(ptr);
}

/*-------------------
	StompAllocator
-------------------*/

void* StompAllocator::Alloc(int32 size)
{
	int32 memFd = ::open(MEMFD, O_RDWR | O_SYNC);
	const int64 pageCount = (size + PAGE_SIZE - 1) / PAGE_SIZE;
	const int64 dataOffset = pageCount * PAGE_SIZE - size;
	void* baseAddress = mmap(NULL, pageCount * PAGE_SIZE, PROT_READ | PROT_WRITE, MAP_SHARED, memFd, 0);
	::close(memFd);
	return static_cast<void*>(static_cast<int8*>(baseAddress) + dataOffset);
}

void StompAllocator::Release(void* ptr)
{
	const int64 address = reinterpret_cast<int64>(ptr);
	const int64 baseAddress = address - (address % PAGE_SIZE);
	const int64 pageCount = (address - baseAddress + PAGE_SIZE - 1) / PAGE_SIZE;
	ASSERT_CRASH(munmap(reinterpret_cast<void*>(baseAddress), pageCount * PAGE_SIZE) >= 0);
}

/*-------------------
	PoolAllocator
-------------------*/

void* PoolAllocator::Alloc(int32 size)
{
	return GMemory->Allocate(size);
}

void PoolAllocator::Release(void* ptr)
{
	GMemory->Release(ptr);
}