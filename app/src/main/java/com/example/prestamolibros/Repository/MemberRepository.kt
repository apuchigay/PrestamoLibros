package com.example.prestamolibros.Repository

import com.example.prestamolibros.DAO.MemberDao
import com.example.prestamolibros.model.Member
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

class MemberRepository(private val memberDao: MemberDao) {

    suspend fun insertMember(member: Member) {
        withContext(Dispatchers.IO) {
            memberDao.insertMember(member)
        }
    }

    suspend fun updateMember(member: Member) {
        withContext(Dispatchers.IO) {
            memberDao.updateMember(member)
        }
    }

    suspend fun deleteMember(member: Member) {
        withContext(Dispatchers.IO) {
            memberDao.deleteMember(member)
        }
    }

    suspend fun getAllMembers(): List<Member> {
        return withContext(Dispatchers.IO) {
            memberDao.getAllMembers()
        }
    }

    suspend fun getMemberById(id: Int): Member? {
        return withContext(Dispatchers.IO) {
            memberDao.getMemberById(id)
        }
    }
}