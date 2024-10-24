/**
 * 
 */
package com.amnex.agristack.Enum;

/**
 * @author majid.belim
 *
 */
public enum ChildMenuEnum {

	Add(100001l), Edit(200002l), Delete(300003l), View(400004l);
	private Long value;

	public Long getValue() {
		return this.value;
	}

	ChildMenuEnum(Long id) {
		this.value = id;
	}

}
