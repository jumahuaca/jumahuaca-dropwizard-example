package org.jumahuaca.examples.jdbc.dao;

import java.time.LocalDate;
import java.util.List;

import org.jumahuaca.examples.jdbc.model.UVAExchange;

public class JdbcExchangeDaoInvocation implements DaoInvocation<UVAExchange, LocalDate> {

	private UvaExchangeDao dao;

	public JdbcExchangeDaoInvocation(UvaExchangeDao dao) {
		this.dao = dao;

	}

	@Override
	public List<UVAExchange> invokeDaoSearchAll() {
		return dao.searchAll();
	}

	@Override
	public UVAExchange invokeDaoFindById(LocalDate id) {
		return dao.findExchangeByDay(id);
	}

	@Override
	public void invokeDaoCreate(UVAExchange entity) {
		dao.create(entity);
	}

	@Override
	public void invokeDaoUpdate(UVAExchange entity) {
		dao.update(entity);
	}

	@Override
	public void invokeDaoDelete(UVAExchange entity) {
		dao.delete(entity);
	}

}
