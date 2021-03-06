package com.ubt.alpha1e.edu.ui.dialog;

import com.ubt.alpha1e.edu.ui.helper.ActionsHelper.Action_type;

public interface IActionListeter {

	void onRenameAction(Object mInfo, Action_type mtype, String new_name);

	void noteRenameIsEmpty();

	void onDelAction(Object mInfo, Action_type mtype, String string);

	void noteRenameIsTooLong();

	void noteRenameHasSpc();

}
