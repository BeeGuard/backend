package com.example.pnapibackend.model.admin.tempaccount;

import java.util.List;

public record GetTempAccountResponse(List<TempAccountResponse> accounts, int pageNumber) {
}
