package com.ssafyhome.community.board.dao;

import com.ssafyhome.community.board.dto.AllBoardDto;
import com.ssafyhome.community.board.dto.Board;
import com.ssafyhome.user.dto.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface BoardRepository extends JpaRepository<Board, Integer> {
	@Query("SELECT new com.ssafyhome.community.board.dto.AllBoardDto(" +
		       "b.boardIdx, b.boardTitle, u.name, b.boardRegDate, b.boardCategory, u.profile, MIN(i.imageUrl)) " +
		       "FROM Board b " +
		       "JOIN b.user u " +
		       "LEFT JOIN b.images i " +
		       "GROUP BY b.boardIdx, b.boardTitle, u.name, b.boardRegDate, b.boardCategory, u.profile")
	List<AllBoardDto> findAllBoards();

    boolean existsByBoardIdxAndUser(int boardIdx, User user);

}