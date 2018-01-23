package net.aza.vaadin.data;

import java.util.List;
import java.util.stream.Collectors;

import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.domain.Sort.Order;

import com.vaadin.data.provider.Query;
import com.vaadin.data.provider.QuerySortOrder;
import com.vaadin.shared.data.sort.SortDirection;

/**
 * A simple Spring Data Pageable implementation to support the lazy loading feature of vaadin 8 data providers / grids in combination with a Spring data repository.
 * <p/>
 * You can simply pass the query or the current offset and requested limit provided by the data provider.
 * <p/>
 * <b>Example (DataProvider)</b>
 * <pre>
 * 	DataProvider.fromCallbacks(
 * 		query -> repository.findAll(DataProviderPageable.of(query)).stream(),
 * 		query -> repository.countAll()
 * 	);
 * </pre>
 * <p/>
 * <b>Example (Grid)</b>
 * <pre>
 * 	this.grid.setDataProvider(
 * 		(order, offset, limit) -> repository.findAll(new VaadinDataProviderPageRequest(offset, limit, order)).stream,
 * 		() -> repository.countAll()
 * 	);</pre>
 *
 *
 *
 * @author azaberlin
 *
 */
public class DataProviderPageable implements Pageable {

	private int offset;
	private int limit;
	private Sort sort = Sort.unsorted();

	/**
	 * Creates a new instance without any order information.
	 * @param offset - index of first item to fetch
	 * @param limit - number of elements to fetch
	 * @return pageable instance
	 */
	public static Pageable of(final int offset, final int limit) {
		return new DataProviderPageable(offset, limit);
	}

	/**
	 * Creates a new instance including order information. The order information may be empty or null. In this case the data will not be sorted.
	 * @param offset - index of first item to fetch
	 * @param limit - number of elements to fetch
	 * @param orderInformationList - list of order information
	 * @return pageable instance
	 */
	public static Pageable of(final int offset, final int limit, final List<QuerySortOrder> orderInformationList) {
		return new DataProviderPageable(offset, limit, orderInformationList);
	}

	/**
	 * Creates a new instance based on the given data provider query.
	 * @param query - query
	 * @return pageable instance
	 */
	public static Pageable of(final Query<?, ?> query) {
		if (query == null) {
			throw new IllegalArgumentException("query must not be null!");
		}
		return DataProviderPageable.of(query.getOffset(), query.getLimit(), query.getSortOrders());
	}

	/**
	 * @see #of(int, int)
	 * @param offset - offset
	 * @param limit - limit
	 */
	private DataProviderPageable(final int offset, final int limit) {
		this.offset = offset;
		this.limit = limit;
	}

	/**
	 * @see #of(int, int, List)
	 * @param offset - offset
	 * @param limit - limit
	 * @param orderInformationList - order information
	 */
	private DataProviderPageable(final int offset, final int limit, final List<QuerySortOrder> orderInformationList) {
		this(offset, limit);

		if (orderInformationList != null) {
			this.sort = Sort.by(convertOrderSettings(orderInformationList));
		}
	}

	/**
	 * Creates a list of Spring Data order information based on the given Vaadin order information.
	 * @param orderInformationList - vaadin order information
	 * @return list of Spring order information
	 */
	private List<Order> convertOrderSettings(final List<QuerySortOrder> orderInformationList) {
		return orderInformationList.stream().map(vaadinOrder -> mapVaadinOrderToSpringOrder(vaadinOrder)).collect(Collectors.toList());
	}

	/**
	 * Map a single Vaadin order information to a single Spring Data order information.
	 * @param orderInformation - vaadin order information
	 * @return Spring order information
	 */
	private Order mapVaadinOrderToSpringOrder(final QuerySortOrder orderInformation) {
		String vaadinName = orderInformation.getSorted();
		SortDirection vaadinDirection = orderInformation.getDirection();

		return vaadinDirection == SortDirection.DESCENDING ? Order.desc(vaadinName) : Order.asc(vaadinName);
	}

	@Override
	public boolean isPaged() {
		return false; // vaadin data provider does not page directly
	}

	@Override
	public int getPageNumber() {
		return 0; // not needed for our purposes
	}

	@Override
	public int getPageSize() {
		return this.limit;
	}

	@Override
	public long getOffset() {
		return this.offset;
	}

	@Override
	public Sort getSort() {
		return this.sort;
	}

	@Override
	public Pageable next() {
		throw new UnsupportedOperationException(); // not needed for our purposes
	}

	@Override
	public Pageable previousOrFirst() {
		throw new UnsupportedOperationException(); // not needed for our purposes
	}

	@Override
	public Pageable first() {
		throw new UnsupportedOperationException(); // not needed for our purposes
	}

	@Override
	public boolean hasPrevious() {
		return false; // not needed for our purposes
	}
}
