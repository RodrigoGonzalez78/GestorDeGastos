package com.example.gestordegastos.domain.usecase

import androidx.paging.PagingData
import com.example.gestordegastos.domain.model.Operation
import com.example.gestordegastos.domain.repository.OperationRepository
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

class GetPagedOperationsUseCase @Inject constructor(
    private val repository: OperationRepository
) {
    operator fun invoke(typeId: Int, startDate: String, endDate: String): Flow<PagingData<Operation>> {
        return repository.getOperations(typeId, startDate, endDate)
    }
}